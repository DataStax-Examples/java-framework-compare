package com.datastax.examples.spring;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "issue")
@Data
@NoArgsConstructor
@AllArgsConstructor
class Issue {
    @PrimaryKey
    private UUID id;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String name;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String description;

    void partialUpdate(Issue partialIssue) {
        if (partialIssue.getName() != null) {
            this.name = partialIssue.getName();
        }

        if (partialIssue.getDescription() != null) {
            this.description = partialIssue.getDescription();
        }
    }
}
