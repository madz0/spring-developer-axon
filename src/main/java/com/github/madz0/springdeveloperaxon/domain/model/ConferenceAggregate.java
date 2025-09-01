package com.github.madz0.springdeveloperaxon.domain.model;

import static com.github.madz0.springdeveloperaxon.domain.util.StringUtils.isEmpty;

import com.github.madz0.springdeveloperaxon.domain.command.CreateConferenceCommand;
import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreatedEvent;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class ConferenceAggregate {
  @AggregateIdentifier private String id;
  private String name;

  @CommandHandler
  public ConferenceAggregate(CreateConferenceCommand ccc) {
    if (ccc.name().equals("shit")) {
      throw new CommandExecutionException("error creating confluence shit!", null);
    }
    AggregateLifecycle.apply(new ConferenceCreatedEvent(ccc.id(), ccc.name()));
  }

  @EventSourcingHandler
  public void handler(ConferenceCreatedEvent cce) {
    this.id = cce.id();
    this.name = cce.name();
  }

  public String getName() {
    return name;
  }
}
