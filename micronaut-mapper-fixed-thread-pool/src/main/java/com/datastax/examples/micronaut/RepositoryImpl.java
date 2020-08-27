package com.datastax.examples.micronaut;

import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;

public class RepositoryImpl implements Repository {
	@Inject
	private IssueDao issueDao;


	@Override public Optional<Issue> findById(UUID id) {
		return Optional.ofNullable(issueDao.findById(id).one());
	}

	@Override public Issue insert(Issue body) {
		issueDao.insert(body);
		return findById(body.getId()).orElseThrow(() -> new RuntimeException("Issue: " + body + " was not inserted."));
	}

	@Override public Issue update(Issue body) {
		issueDao.update(body);
		return findById(body.getId()).orElseThrow(() -> new RuntimeException("Issue: " + body + " was not updated."));
	}

	@Override public void deleteById(UUID id) {
		issueDao.deleteById(id);
	}

	public void deleteAll(){
		issueDao.deleteAll();
	}

	@Override public Iterable<Issue> findAll() {
		return issueDao.findAll();
	}
}
