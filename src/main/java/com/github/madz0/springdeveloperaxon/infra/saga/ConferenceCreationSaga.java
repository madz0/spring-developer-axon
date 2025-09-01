package com.github.madz0.springdeveloperaxon.infra.saga;

import com.github.madz0.springdeveloperaxon.domain.command.CancelConferenceCreationCommand;
import com.github.madz0.springdeveloperaxon.domain.command.SendWelcomeAnnouncementCommand;
import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreatedEvent;
import com.github.madz0.springdeveloperaxon.domain.event.ConferenceCreationCancelledEvent;
import com.github.madz0.springdeveloperaxon.domain.event.WelcomeAnnouncementFailedEvent;
import com.github.madz0.springdeveloperaxon.domain.event.WelcomeAnnouncementSentEvent;
import java.time.Duration;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.stereotype.Component;

@Saga
@Component
public class ConferenceCreationSaga {

  // You will need these injected to send commands and manage time
  private final transient CommandGateway commandGateway;
  private final transient DeadlineManager deadlineManager;

  private String conferenceId;
  private String deadlineId;

  public ConferenceCreationSaga(CommandGateway commandGateway, DeadlineManager deadlineManager) {
    this.commandGateway = commandGateway;
    this.deadlineManager = deadlineManager;
  }

  // ✅ 1. SAGA STARTS
  // The Saga begins when a conference is successfully created.
  @StartSaga
  @SagaEventHandler(associationProperty = "id")
  public void on(ConferenceCreatedEvent event) {
    this.conferenceId = event.id();

    // ⏱️ 2. SET A TIMEOUT
    // Schedule a deadline. If we don't hear back in 30 seconds,
    // the @DeadlineHandler method will be triggered.
    this.deadlineId =
        deadlineManager.schedule(
            Duration.ofSeconds(30), "announcement-timeout" // A descriptive name for the deadline
            );

    // 3. DISPATCH NEXT COMMAND
    // Send the command to the next service in the process.
    commandGateway.send(new SendWelcomeAnnouncementCommand(event.id(), event.name()));
  }

  // ✅ 4. HAPPY PATH: SAGA ENDS
  // This handler is called if the announcement was sent successfully.
  @EndSaga
  @SagaEventHandler(associationProperty = "conferenceId")
  public void on(WelcomeAnnouncementSentEvent event) {
    // The process succeeded, so we cancel the timeout deadline.
    deadlineManager.cancelSchedule(this.deadlineId, "announcement-timeout");
  }

  /*
  if instead of WelcomeAnnouncementSentEvent, we received WelcomeAnnouncementFailedEvent,
  that means need to send a command to cancel the conference. But saga is not end.
  Because need to receive the ConferenceCreationCancelledEvent to confirm
   */
  @SagaEventHandler(associationProperty = "conferenceId")
  public void on(WelcomeAnnouncementFailedEvent event) {
    commandGateway.send(
        new CancelConferenceCreationCommand(
            event.conferenceId(), "Cancelling flight due to failed hotel booking."));
  }

  /*
  if didn't receive either of WelcomeAnnouncementFailedEvent or WelcomeAnnouncementSentEvent
  means something went wrong, and we should send the CancelConferenceCreationCommand
  because of deadline
   */
  @DeadlineHandler(deadlineName = "announcement-timeout")
  public void onAnnouncementTimeout() {
    // The announcement failed. Dispatch the compensating command.
    commandGateway.send(
        new CancelConferenceCreationCommand(
            this.conferenceId, "Failed to send welcome announcement in time."));
  }

  // ❌ 6. FAILURE PATH: SAGA ENDS
  // The Saga ends after the cancellation is confirmed.
  @EndSaga
  @SagaEventHandler(associationProperty = "conferenceId")
  public void on(ConferenceCreationCancelledEvent event) {
    // Saga is now complete after the compensating action.
  }
}
