package lizzy.medium.compare.helidon;

import java.nio.file.Paths;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.internal.core.auth.ProgrammaticPlainTextAuthProvider;
import io.helidon.config.Config;

@ApplicationScoped
@Provider
public class CqlSessionFactory {

	@Singleton
	CqlSession cqlSession() {
		Config config = Config.builder().build();

		return new CqlSessionBuilder().withCloudSecureConnectBundle(
				Paths.get(config.get("cassandra.secure-connect-bundle").asString().get()))
				.withKeyspace(config.get("cassandra.keyspace").asString().get())
				.withAuthProvider(
						new ProgrammaticPlainTextAuthProvider(
								config.get("username").asString().get(), config.get("password").asString().get()))
				.build();
	}
}
