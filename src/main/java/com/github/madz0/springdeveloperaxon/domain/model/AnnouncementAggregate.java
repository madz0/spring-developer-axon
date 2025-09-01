package com.github.madz0.springdeveloperaxon.domain.model;

import com.github.madz0.springdeveloperaxon.domain.command.SendWelcomeAnnouncementCommand;
import com.github.madz0.springdeveloperaxon.domain.event.WelcomeAnnouncementFailedEvent;
import com.github.madz0.springdeveloperaxon.domain.event.WelcomeAnnouncementSentEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate
public class AnnouncementAggregate {

    @AggregateIdentifier
    private String announcementId;
    private String conferenceId;
    private boolean sentSuccessfully;

    // ✅ 1. This is the command handler
    // It creates a new AnnouncementAggregate instance.
    @CommandHandler
    public AnnouncementAggregate(SendWelcomeAnnouncementCommand command) {
        System.out.println("Received command to send announcement for conference: " + command.conferenceId());

        // In a real application, this is where you would interact with an
        // external service (e.g., an email or notification service).
        
        // Let's simulate a potential failure.
        if (command.conferenceName().toLowerCase().contains("fail")) {
            System.out.println("Simulating a failure to send announcement.");
            // Publish a failure event that the Saga can listen for.
            AggregateLifecycle.apply(new WelcomeAnnouncementFailedEvent(
                command.conferenceId(), 
                "The external notification service rejected the request."
            ));
        } else {
            System.out.println("Successfully sent the announcement!");
            // On success, publish the event that the Saga is waiting for.
            AggregateLifecycle.apply(new WelcomeAnnouncementSentEvent(
                command.conferenceId(),
                UUID.randomUUID().toString() // The new announcement's ID
            ));
        }
    }

    // 2. The state is updated from the success event
    @EventSourcingHandler
    public void on(WelcomeAnnouncementSentEvent event) {
        this.announcementId = event.announcementId();
        this.conferenceId = event.conferenceId();
        this.sentSuccessfully = true;
    }

    // (Optional) Handle the failure event to update state if needed
    @EventSourcingHandler
    public void on(WelcomeAnnouncementFailedEvent event) {
        this.announcementId = event.announcementId();
        this.conferenceId = event.conferenceId();
        this.sentSuccessfully = false;
    }

    // Required by Axon
    protected AnnouncementAggregate() {}
}