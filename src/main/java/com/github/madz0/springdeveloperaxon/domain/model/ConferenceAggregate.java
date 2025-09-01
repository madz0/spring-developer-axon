package com.github.madz0.springdeveloperaxon.domain.model;

import com.github.madz0.springdeveloperaxon.domain.command.CreateConferenceCommand;
import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class ConferenceAggregate {
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
