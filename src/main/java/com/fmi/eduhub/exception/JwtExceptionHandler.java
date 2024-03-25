package com.fmi.eduhub.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtExceptionHandler {

  @ExceptionHandler({JwtException.class})
  public ResponseEntity<RestError> jwtExceptionHandling(
      JwtException jwtException,
      HttpServletRequest request) {
    RestError restError = RestError.builder()
        .status(HttpStatus.UNAUTHORIZED)
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .message("JWT Exception")
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restError);
  }

  @ExceptionHandler({CustomJwtException.class})
  public ResponseEntity<RestError> handleCustomJwtException(
      JwtException jwtException,
      HttpServletRequest request) {
    RestError restError = RestError.builder()
        .status(HttpStatus.UNAUTHORIZED)
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .message(jwtException.getMessage())
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restError);
  }

  @ExceptionHandler({UnsupportedJwtException.class})
  public ResponseEntity<RestError> handleUnsupportedJwtException(
      UnsupportedJwtException jwtException,
      HttpServletRequest request) {
    RestError restError = RestError.builder()
        .status(HttpStatus.FORBIDDEN)
        .statusCode(HttpStatus.FORBIDDEN.value())
        .message(ExceptionConstants.UNSUPPORTED_TOKEN)
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restError);
  }


  @ExceptionHandler({MalformedJwtException.class})
  public ResponseEntity<RestError> handleMalformedJwtException(
      MalformedJwtException jwtException,
      HttpServletRequest request) {
    RestError restError = RestError.builder()
        .status(HttpStatus.BAD_REQUEST)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .message(ExceptionConstants.INVALID_TOKEN)
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restError);
  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<RestError> accessDeniedExceptionHandling(
      AccessDeniedException accessDeniedException, HttpServletRequest httpServletRequest) {
    RestError restError = RestError.builder()
        .status(HttpStatus.FORBIDDEN)
        .statusCode(HttpStatus.FORBIDDEN.value())
        .message(ExceptionConstants.ACCESS_DENIED)
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restError);
  }

  @ExceptionHandler({ExpiredJwtException.class})
  public ResponseEntity<RestError> jwtTokenExpiredHandling(
      ExpiredJwtException exception, HttpServletRequest request) {
    RestError restError = RestError.builder()
        .status(HttpStatus.UNAUTHORIZED)
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .message(ExceptionConstants.TOKEN_EXPIRED)
        .timeStamp(OffsetDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restError);
  }


}
