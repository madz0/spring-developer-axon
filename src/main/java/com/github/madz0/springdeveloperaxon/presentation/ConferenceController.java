package com.github.madz0.springdeveloperaxon.presentation;

import com.github.madz0.springdeveloperaxon.domain.command.CreateConferenceCommand;
import com.github.madz0.springdeveloperaxon.domain.model.Conference;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/conferences")
class ConferenceController {

  private static final Logger log = LoggerFactory.getLogger(ConferenceController.class);

  final CommandGateway commandGateway;
  final QueryGateway queryGateway;

  ConferenceController(CommandGateway commandGateway, QueryGateway queryGateway) {
    this.commandGateway = commandGateway;
    this.queryGateway = queryGateway;
  }

  @PostMapping
  CompletableFuture<ResponseEntity<Object>> write(
      @RequestParam String id, @RequestParam String name) {
    return commandGateway
        .send(new CreateConferenceCommand(id, name))
        .thenApply(
            aggregateId ->
                ResponseEntity.created(URI.create("/conferences/" + aggregateId)).build());
  }

  @GetMapping
  CompletableFuture<List<Conference>> read() {
    return queryGateway.query(
        "allConferences", null, ResponseTypes.multipleInstancesOf(Conference.class));
  }
}
