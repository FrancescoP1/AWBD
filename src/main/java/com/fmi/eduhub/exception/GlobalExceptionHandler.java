package com.fmi.eduhub.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({UsernameNotFoundException.class})
  public ResponseEntity<RestError> handleUsernameNotFoundException(
      UsernameNotFoundException exception) {
    RestError restError = RestError.builder()
        .status(HttpStatus.NOT_FOUND)
        .statusCode(HttpStatus.NOT_FOUND.value())
        .message(exception.getMessage())
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restError);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<RestError> handleResourceNotFoundException(
      ResourceNotFoundException exception) {
    RestError restError = RestError.builder()
        .status(HttpStatus.NOT_FOUND)
        .statusCode(HttpStatus.NOT_FOUND.value())
        .message(exception.getMessage())
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restError);
  }

  @ExceptionHandler({ResourceNotAccessibleException.class})
  public ResponseEntity<RestError> handleResourceNotAccessible(
      ResourceNotAccessibleException exception) {
    RestError restError = RestError.builder()
        .status(HttpStatus.FORBIDDEN)
        .statusCode(HttpStatus.FORBIDDEN.value())
        .message(exception.getMessage())
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restError);
  }

  @ExceptionHandler({BadUserRequestException.class})
  public ResponseEntity<RestError> badUserRequestException(BadUserRequestException exception) {
    log.error(exception.toString());
    RestError restError = RestError.builder()
        .status(HttpStatus.BAD_REQUEST)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .message(exception.getMessage())
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(restError);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<RestError> handleException(Exception exception) {
    log.error(exception.toString());
    RestError restError = RestError.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(ExceptionConstants.SOMETHING_WENT_WRONG)
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(restError);
  }
}
