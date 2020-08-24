package com.datastax.examples.micronaut;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface IssueMapper {

	@DaoFactory
	IssueDao issueDao();
}