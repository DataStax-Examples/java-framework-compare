package lizzy.medium.compare.spring;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SessionBuilderConfigurer;


@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
public class SpringDataCassandraConfiguration extends AbstractCassandraConfiguration {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Value("${astra.secure-connect-bundle}")
    private String astraSecureConnectBundle;


    @Override
    protected String getKeyspaceName() {
        return cassandraProperties.getKeyspaceName();
    }

    @Override
    protected String getLocalDataCenter() {
        return cassandraProperties.getLocalDatacenter();
    }

    @Override
    protected int getPort() {
        return cassandraProperties.getPort();
    }

    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return cqlSessionBuilder -> cqlSessionBuilder
                .withCloudSecureConnectBundle(Paths.get(astraSecureConnectBundle))
                .withKeyspace(cassandraProperties.getKeyspaceName())
                .withAuthCredentials(cassandraProperties.getUsername(), cassandraProperties.getPassword());
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{ "lizzy.medium.compare.spring" };
    }
}
