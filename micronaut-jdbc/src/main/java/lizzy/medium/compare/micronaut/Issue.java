package com.datastax.examples.micronaut;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Issue {
    private UUID id;
    private String name;
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
