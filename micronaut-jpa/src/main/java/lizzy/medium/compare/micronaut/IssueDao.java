package com.datastax.examples.micronaut;

import java.util.UUID;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;

@Dao
public interface IssueDao {


	@Select
	PagingIterable<Issue> findById(UUID id);

	@Insert
	void insert(Issue fruit);

	@Update
	void update(Issue fruit);

	@Delete(entityClass = Issue.class)
	void deleteById(UUID id);

	@Select
	PagingIterable<Issue> findAll();
}