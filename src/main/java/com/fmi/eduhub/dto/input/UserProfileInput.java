package com.fmi.eduhub.dto.input;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileInput {
  private String userId;
  private String firstName;
  private String lastName;
  private String email;
  private String userType;
  private MultipartFile profileImage;
}
