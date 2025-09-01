package com.github.madz0.springdeveloperaxon.infra.query;

import com.github.madz0.springdeveloperaxon.domain.model.Conference;
import java.util.List;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class ConferenceQueryHandler {
  private final JdbcClient jdbcClient;

  ConferenceQueryHandler(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @QueryHandler(queryName = "allConferences")
  List<Conference> handleQuery() {
    return jdbcClient
        .sql(
            """
          select * from conference
        """)
        .query((rs, i) -> new Conference(rs.getString("id"), rs.getString("name")))
        .list();
  }
}
