package com.github.madz0.springdeveloperaxon.infra.query;

import com.github.madz0.springdeveloperaxon.application.ConferenceInfoService;
import com.github.madz0.springdeveloperaxon.domain.model.Conference;
import java.util.List;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class ConferenceQueryHandler implements ConferenceInfoService {
  private final JdbcClient jdbcClient;

  ConferenceQueryHandler(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  @QueryHandler(queryName = "allConferences")
  public List<Conference> getAllConferences() {
    return jdbcClient
        .sql(
            """
          select * from conference
        """)
        .query((rs, i) -> new Conference(rs.getString("id"), rs.getString("name")))
        .list();
  }
}
