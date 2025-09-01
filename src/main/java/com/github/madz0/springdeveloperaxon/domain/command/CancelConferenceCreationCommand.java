package com.github.madz0.springdeveloperaxon.domain.command;

public record CancelConferenceCreationCommand(String conferenceId, String reason) {}