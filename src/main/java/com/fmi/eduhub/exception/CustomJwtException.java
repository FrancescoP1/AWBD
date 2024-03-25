package com.fmi.eduhub.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomJwtException extends JwtException {
  public CustomJwtException(String message) {
    super(message);
  }

  public CustomJwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
