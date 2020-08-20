# medium-java-framework-compare

## Pre-requisites
Before running the 
```
cd compare
./measure.sh
```

1. Install the jmeter tool and make it available via global `jmeter` command;
This tool is using `jmeter` for load-testing, i.e: `jmeter -n -t loadtest.jmx -j jmeter.out -l jmeter.log` 
  

2. set the ENV variable `ASTRA_PATH` to the location of the secure connect bundle on your local file system.
```
export ASTRA_PATH=/path/to/your/local/secure-connect-framework-compare.zip 
```