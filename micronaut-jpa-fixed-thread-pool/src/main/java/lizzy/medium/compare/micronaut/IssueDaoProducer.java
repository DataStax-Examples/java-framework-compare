package lizzy.medium.compare.micronaut;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.datastax.oss.driver.api.core.CqlSession;
import io.micronaut.context.annotation.Factory;
import io.micronaut.http.annotation.Produces;

@Factory
public class IssueDaoProducer {

  private final IssueDao issueDao;

  @Inject
  public IssueDaoProducer(CqlSession session) {
    IssueMapper mapper = new IssueMapperBuilder(session).build();
    issueDao = mapper.issueDao();
  }

  @Produces
  @Singleton
  IssueDao produceFruitDao() {
    return issueDao;
  }
}
