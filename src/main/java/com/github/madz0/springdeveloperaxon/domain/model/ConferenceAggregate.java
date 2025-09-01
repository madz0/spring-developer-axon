package com.github.madz0.springdeveloperaxon.domain.model;

import com.github.madz0.springdeveloperaxon.domain.command.CancelConferenceCreationCommand;
import com.github.madz0.springdeveloperaxon.domain.command.CreateConferenceCommand;
import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreatedEvent;
import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreationCancelledEvent;
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
  private boolean isCancelled;

  @CommandHandler
  public ConferenceAggregate(CreateConferenceCommand ccc) {
    if (ccc.name().equals("shit")) {
      throw new CommandExecutionException("error creating confluence shit!", null);
    }
    AggregateLifecycle.apply(new ConferenceCreatedEvent(ccc.id(), ccc.name()));
  }

  @EventSourcingHandler
  public void on(ConferenceCreatedEvent cce) {
    this.id = cce.id();
    this.name = cce.name();
    this.isCancelled = false;
  }

  // Add a handler for the compensating command
  @CommandHandler
  public void handle(CancelConferenceCreationCommand command) {
    if (isCancelled) {
      return; // Already cancelled, do nothing.
    }
    AggregateLifecycle.apply(new ConferenceCreationCancelledEvent(command.conferenceId()));
  }

  @EventSourcingHandler
  public void on(ConferenceCreationCancelledEvent event) {
    this.isCancelled = true;
  }
}
