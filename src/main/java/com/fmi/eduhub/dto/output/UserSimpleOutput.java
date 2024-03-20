package com.fmi.eduhub.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSimpleOutput {
  private String userId;
  private String firstName;
  private String lastName;
  private String profilePictureUrl;
}
