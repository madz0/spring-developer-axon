package com.github.madz0.springdeveloperaxon.infra.projection;

import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreatedEvent;
import com.github.madz0.springdeveloperaxon.domain.model.Conference;
import java.util.List;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
class ConferenceProjectionHandler {
  private final JdbcClient jdbcClient;

  ConferenceProjectionHandler(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @EventHandler
  void on(ConferenceCreatedEvent conferenceCreatedEvent) {
    jdbcClient
        .sql(
            """
          insert into conference(id, name) values(?,?)
        """)
        .param(conferenceCreatedEvent.id())
        .param(conferenceCreatedEvent.name())
        .update();
  }
}
