package lizzy.medium.compare.spring;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
interface IssueRepository extends CassandraRepository<Issue, UUID> {
}
