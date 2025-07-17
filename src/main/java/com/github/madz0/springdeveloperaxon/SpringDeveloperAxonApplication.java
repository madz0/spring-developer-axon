package com.github.madz0.springdeveloperaxon;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class SpringDeveloperAxonApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringDeveloperAxonApplication.class, args);
  }

  @Bean
  InitializingBean initializingBean(ObjectMapper objectMapper) {
    return () ->
        objectMapper.activateDefaultTyping(
            objectMapper.getPolymorphicTypeValidator(), DefaultTyping.JAVA_LANG_OBJECT);
  }

  @Aggregate
  public static class ConferenceAggregate {
    @AggregateIdentifier private String id;

    @CommandHandler
    public ConferenceAggregate(CreateConferenceCommand ccc) {
      AggregateLifecycle.apply(new ConferenceCreatedEvent(ccc.id(), ccc.name()));
    }

    @EventSourcingHandler
    public void handler(ConferenceCreatedEvent cce) {
      this.id = cce.id();
    }
  }
}

@Component
class ConferenceProjectionHandler {
  private final JdbcClient jdbcClient;

  ConferenceProjectionHandler(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @EventHandler
  void handleWrite(ConferenceCreatedEvent conferenceCreatedEvent) {
    jdbcClient
        .sql("""
          insert into conference(id, name) values(?,?)
        """)
        .param(conferenceCreatedEvent.id())
        .param(conferenceCreatedEvent.name())
        .update();
  }

  @QueryHandler(queryName = "allConferences")
  List<Conference> handleQuery() {
    return jdbcClient
        .sql("""
          select * from conference
        """)
        .query((rs, i) -> new Conference(rs.getString("id"), rs.getString("name")))
        .list();
  }
}

@Controller
@ResponseBody
@RequestMapping("/conferences")
class ConferenceController {

  final CommandGateway commandGateway;
  final QueryGateway queryGateway;

  ConferenceController(CommandGateway commandGateway, QueryGateway queryGateway) {
    this.commandGateway = commandGateway;
    this.queryGateway = queryGateway;
  }

  @PostMapping
  CompletableFuture<String> write(@RequestParam String id, @RequestParam String name) {
    return commandGateway.send(new CreateConferenceCommand(id, name));
  }

  @GetMapping
  CompletableFuture<List<Conference>> read() {
    return queryGateway.query(
        "allConferences", null, ResponseTypes.multipleInstancesOf(Conference.class));
  }
}

record CreateConferenceCommand(String id, String name) {}

record ConferenceCreatedEvent(String id, String name) {}

record Conference(String id, String name) {}
