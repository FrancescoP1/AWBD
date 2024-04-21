package com.fmi.eduhub.exception;

public class BadUserRequestException extends RuntimeException {
  public BadUserRequestException(String message) {
    super(message);
  }
}
