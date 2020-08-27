package com.datastax.examples.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/issue")
public class IssueController {
    private final IssueRepository repository;

    @Autowired
    public IssueController(IssueRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Issue> readAll() {
        List<Issue> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    @GetMapping("/{id}")
    public Optional<Issue> read(@PathVariable("id") UUID id) {
        return repository.findById(id);
    }

    @PostMapping
    public Issue create(@RequestBody Issue body) {
        return repository.save(body);
    }

    @PutMapping("/{id}")
    public Issue update(@PathVariable("id") UUID id, @RequestBody Issue body) {
        return repository.save(body);
    }

    @PatchMapping("/{id}")
    public Issue partialUpdate(@PathVariable("id") UUID id, @RequestBody Issue body) {
        final Issue issue = repository.findById(id).orElseThrow(RuntimeException::new);
        issue.partialUpdate(body);
        return repository.save(issue);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        repository.deleteById(id);
    }

    @DeleteMapping
    public void deleteAll() {
        repository.deleteAll();
    }
}
