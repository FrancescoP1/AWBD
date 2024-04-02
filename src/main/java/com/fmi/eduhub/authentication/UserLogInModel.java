package com.fmi.eduhub.authentication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLogInModel {
  private String email;
  private String password;
}
