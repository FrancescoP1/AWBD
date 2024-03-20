package com.fmi.eduhub.dto.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileOutput {
  private String userId;
  private String firstName;
  private String lastName;
  private String email;
  private String userType;
  private String profilePictureUrl;



  boolean canEditAdmin = false;
  boolean canDelete = false;
  boolean canEditUser = false;
}
