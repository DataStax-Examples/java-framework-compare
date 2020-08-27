package com.datastax.examples.helidon;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.internal.core.auth.ProgrammaticPlainTextAuthProvider;
import io.helidon.config.Config;

@ApplicationScoped
public class RepositoryImpl implements Repository {

    private CqlSession cqlSession;

    @PostConstruct
    public void setup() {
        Config config = Config.builder().build();

        this.cqlSession = new CqlSessionBuilder().withCloudSecureConnectBundle(
                Paths.get(config.get("cassandra.secure-connect-bundle").asString().get()))
                .withKeyspace(config.get("cassandra.keyspace").asString().get())
                .withAuthProvider(
                        new ProgrammaticPlainTextAuthProvider(
                                config.get("cassandra.username").asString().get(), config.get("cassandra.password").asString().get()))
                .build();
    }


    @Override
    public Optional<Issue> findById(UUID id) {
        return Optional.ofNullable(
                cqlSession.execute(SimpleStatement.newInstance("SELECT * from issue where id = ?", id)).one()
        ).map(this::mapToIssue);
    }

    @Override
    public Issue insert(Issue issue) {
        cqlSession.execute(SimpleStatement.newInstance("INSERT INTO issue(id, name, description) VALUES (?,?,?)", issue.getId(), issue.getName(), issue.getDescription()));
        return issue;
    }

    @Override
    public void deleteById(UUID id) {
        cqlSession.execute(SimpleStatement.newInstance("DELETE from issue where id = ?", id));
    }

    public void deleteAll() {
        cqlSession.execute(SimpleStatement.newInstance("TRUNCATE issue"));
    }

    @Override
    public List<Issue> findAll() {
        ResultSet result = cqlSession.execute(SimpleStatement.newInstance("SELECT * FROM issue"));
        return result.all().stream().map(this::mapToIssue).collect(Collectors.toList());
    }

    @Override
    public Issue update(Issue issue) {
        cqlSession.execute(SimpleStatement.newInstance("UPDATE issue SET name = ?, description = ? WHERE ID = ?",
                issue.getName(), issue.getDescription(), issue.getId()));
        return findById(issue.getId()).orElseThrow(RuntimeException::new);
    }


    private Issue mapToIssue(Row row) {
        return new Issue(row.getUuid("id"), row.getString("name"), row.getString("description"));
    }
}
