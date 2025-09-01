package com.github.madz0.springdeveloperaxon.presentation;

import com.github.madz0.springdeveloperaxon.domain.exception.CommandCreationException;
import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ConferenceExceptionHandler {

  @ExceptionHandler(CommandExecutionException.class)
  public ProblemDetail handle(CommandExecutionException exception) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
  }

  @ExceptionHandler(CommandCreationException.class)
  public ProblemDetail handle(CommandCreationException exception) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ProblemDetail handle(RuntimeException exception) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
  }
}
