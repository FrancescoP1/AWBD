package com.fmi.eduhub.authentication;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
  private String jwtToken;
  private String refreshToken;
}
