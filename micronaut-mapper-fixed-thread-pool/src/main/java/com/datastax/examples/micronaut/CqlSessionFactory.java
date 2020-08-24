package com.datastax.examples.micronaut;

import java.nio.file.Paths;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.internal.core.auth.ProgrammaticPlainTextAuthProvider;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class CqlSessionFactory {
	@Inject
	private CassandraConfiguration cassandraConfiguration;

	@Singleton
	@Bean
	CqlSession cqlSession() {
		return new CqlSessionBuilder().withCloudSecureConnectBundle(Paths.get(cassandraConfiguration.getSecureConnectBundle()))
				.withKeyspace(cassandraConfiguration.getKeyspace())
				.withAuthProvider(new ProgrammaticPlainTextAuthProvider(cassandraConfiguration.getUsername(), cassandraConfiguration.getPassword()))
				.build();
	}
}
