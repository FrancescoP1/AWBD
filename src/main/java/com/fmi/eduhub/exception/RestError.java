package com.fmi.eduhub.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class RestError {
  private HttpStatus status;
  private Integer statusCode;
  private String message;
  private String detail;
  private OffsetDateTime timeStamp;
}
