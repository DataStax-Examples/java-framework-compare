package com.datastax.examples.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("cassandra")
public class CassandraConfiguration {
	private String keyspace;
	private String secureConnectBundle;
	private String username;
	private String password;

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public String getSecureConnectBundle() {
		return secureConnectBundle;
	}

	public void setSecureConnectBundle(String secureConnectBundle) {
		this.secureConnectBundle = secureConnectBundle;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
