package lizzy.medium.compare.spring;

import java.nio.file.Paths;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.internal.core.auth.ProgrammaticPlainTextAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CqlSessionFactory {

	@Value("${cassandra.astra.secure-connect-bundle}")
	private String astraSecureConnectBundle;
	@Value("${cassandra.keyspace-name}")
	private String keyspace;
	@Value("${cassandra.username}")
	private String username;
	@Value("${cassandra.password}")
	private String password;


	@Bean
	CqlSession cqlSession() {

		return new CqlSessionBuilder().withCloudSecureConnectBundle(
				Paths.get(astraSecureConnectBundle))
				.withKeyspace(keyspace)
				.withAuthProvider(
						new ProgrammaticPlainTextAuthProvider(username, password))
				.build();
	}
}
