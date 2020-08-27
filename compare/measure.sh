#!/bin/bash
COMPILE_TIMES=5
STARTUP_TIMES=5
LOAD_TIMES=5

function check(){
    prepareDocker
    compileTime "$1" "$2" "$3"
    startup     "$2"
    load        "$2"
    cleanDocker "$2"
}

function prepareDocker () {
    printf "*** Preparing Docker ***\n"
    # Delete everything
    printf "~ Stopping and removing containers ~\n"

    docker-compose stop
    if [ $? -ne 0 ]
    then
        fail "Could not stop docker $1"
    fi
    docker-compose rm -f
}

function compileTime(){
    for (( i=0; i<COMPILE_TIMES; i++))
    do
        #make a clean first as we want to measure a full rebuild
        clean "$1"

	#Build the application and store the time needed to results
        startMS=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
        compile "$1" "$3"
        buildImage "$2"
        endMS=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
        compiletime=$((endMS - startMS))
        echo "$2, Compile time, $compiletime milliseconds" >> results.csv
    done
}

function clean() {
    pushd ../$1 > /dev/null
    printf "*** Cleaning $1 ***\n"
    ../mvnw clean --quiet
    if [ $? -ne 0 ]
    then
        popd
        fail "Could not clean folder $1"
    fi
    popd > /dev/null
}

function compile(){
    pushd ../$1 > /dev/null

    printf "*** Packaging $1 ***\n"
    printf "*** This may take a few minutes to pull dependencies ***\n"
    ../mvnw package $2 --quiet
    if [ $? -ne 0 ]
    then
        popd
        fail "Could not build folder $1"
    fi
    popd > /dev/null
}
 
function buildImage(){
    printf "*** Building image for $1 ***\n"
    if [ -z ${ASTRA_DB_BUNDLE} ]
    then
        fail "You must set ASTRA_DB_BUNDLE to the path of your Astra secure connect bundle"
    fi

    docker-compose build $1 > /dev/null
    if [ $? -ne 0 ]
    then
        fail "Could not build image $1"
    fi
}

function startup(){
    for (( start=0; start<STARTUP_TIMES; start++))
    do
        #Recreate the container to always have a startup from null
        disposeContainer "$1"

        #Start the container and measure how long it takes untill we get a valid result
        startMS=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
        startContainer "$1"
        endMS=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
        startuptime=$((endMS - startMS))
	      echo "$1, Startup time, $startuptime milliseconds" >> results.csv

        #Measure memory
        memory=$(docker stats --format "{{.MemUsage}}" --no-stream "compare_$1_1" | awk 'match($0,/[0-9\.]+/) {print substr($0, RSTART, RLENGTH)}')
        echo "$1, Memory Usage (Startup), $memory" >> results.csv

        #Make sure container runs normally
        checkContainer "$1"
    done

    #Stop container again
    disposeContainer "$1"
}

function disposeContainer() {
    printf "*** Stopping $1 ***\n"
    docker-compose stop $1
    printf "*** Removing $1 ***\n"
    docker-compose rm -f $1
}

function startContainer() {
    printf "*** Starting $1 ***\n"
    docker-compose up -d $1
    cameUp=0
    for (( i=0; i<100; i++))
    do
        sleep 0.3
        curl -s -X POST http://localhost:8080/issue/ \
            -d '{"id":"550e8400-e29b-11d4-a716-446655440000","name":"Test", "description":"This is a test"}' \
            -H "Content-Type: application/json" > /dev/null
        curl -s http://localhost:8080/issue/550e8400-e29b-11d4-a716-446655440000/ | grep "This is a test" > /dev/null
        if [ $? -eq 0 ]
        then
            return;
        fi;
    done
    curl http://localhost:8080/issue/550e8400-e29b-11d4-a716-446655440000/ -v
    fail "Container could not start"
}

function checkContainer() {
    curl -s http://localhost:8080/issue/ | grep "This is a test" > /dev/null
    if [ $? -ne 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed GET ALL for $1"
    fi;

    #Create a new entry
    curl -X POST http://localhost:8080/issue/ \
        -d '{"id":"550e8400-e29b-11d4-a728-446655440000","name":"Test 123", "description":"Test 28"}' \
        -H "Content-Type: application/json" 
    curl -s http://localhost:8080/issue/ | grep "Test 28" > /dev/null
    if [ $? -ne 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed CREATE for $1"
    fi;

    #Patch new entry
    curl -X PATCH http://localhost:8080/issue/550e8400-e29b-11d4-a728-446655440000/ \
        -d '{"description":"Test NEW"}' \
       	-H "Content-Type: application/json" 
    curl -s http://localhost:8080/issue/ | grep "Test NEW" > /dev/null
    if [ $? -ne 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed PATCH for $1"
    fi;

    #Delete new entry
    curl -X DELETE http://localhost:8080/issue/550e8400-e29b-11d4-a728-446655440000/
    curl -s http://localhost:8080/issue/ | grep "Test NEW" > /dev/null
    if [ $? -eq 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed DELETE for $1"
    fi;
}

function fail() {
    echo "$1"  1>&2;
    exit -1
}

function load() {
    for (( load=0; load<LOAD_TIMES; load++))
    do
	prepareForLoad "$1"
        startMS=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
        jmeter -n -t loadtest.jmx -j jmeter.out -l jmeter.log
        endMS=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
        memory=$(docker stats --format "{{.MemUsage}}" --no-stream "compare_$1_1" | awk 'match($0,/[0-9\.]+/) {print substr($0, RSTART, RLENGTH)}')
        loadtime=$((endMS - startMS))

        tail -1 jmeter.out | grep "Err: *0 ("
        if [ $? -ne 0 ]
        then
            echo "$1, Memory Usage (Load), FAIL" >> results.csv
            echo "$1, Load Time, FAIL" >> results.csv
	else
            echo "$1, Memory Usage (Load), $memory" >> results.csv
            echo "$1, Load Time, $loadtime milliseconds" >> results.csv
        fi
    done
}

function prepareForLoad() {
    # We have to freshly set up the container (incl db) to avoid follow up effects
    docker-compose stop
    docker-compose rm -f
    sleep 10;
    startContainer "$1"
}

function cleanDocker() {
    printf "*** Cleaning database ***\n"
    curl -X DELETE http://localhost:8080/issue/
    printf "*** Cleaning Docker ***\n"
    printf "~ Stopping compare_$1_1 ~\n"
    docker stop compare_$1_1 > /dev/null
    printf "~ Removing compare_$1_1 ~\n"
    docker rm -f compare_$1_1 > /dev/null
    printf "~ Removing image for compare_$1 ~\n"
    docker rmi -f compare_$1 > /dev/null
    printf "~ Pruning Images ~\n"
    docker image prune -f > /dev/null
    printf "~ Pruning Volumes ~\n"
    docker volume prune -f > /dev/null
}

# Remove the old result file
rm -f results.csv

check "helidon-mp"     "helidon-mp"
check "spring"         "spring"
check "spring-driver"    "spring-driver"
check "quarkus"        "quarkus"
check "micronaut-driver" "micronaut-driver"
check "micronaut-mapper"  "micronaut-mapper"
check "micronaut-driver-fixed-thread-pool" "micronaut-driver-fixed-thread-pool"
check "micronaut-mapper-fixed-thread-pool" "micronaut-mapper-fixed-thread-pool"
check "quarkus"        "quarkus-graal"        "-Pnative -Dquarkus.native.container-build=true"

