package com.fmi.eduhub.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;

@Getter
@Setter
public class ResourceNotAccessibleException extends AccessDeniedException {

  public ResourceNotAccessibleException(String msg) {
    super(msg);
  }
}
