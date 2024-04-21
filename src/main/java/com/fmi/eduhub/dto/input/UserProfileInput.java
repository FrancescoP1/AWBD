package com.fmi.eduhub.dto.input;

import com.fmi.eduhub.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileInput {
  @NotBlank(message = ValidationMessages.USER_PROFILE_ID_NOT_BLANK_MESSAGE)
  private String userId;
  @Size(min = 2, max = 100, message = ValidationMessages.FIRST_NAME_SIZE_VALIDATION_MESSAGE)
  private String firstName;
  @Size(min = 2, max = 100, message = ValidationMessages.LAST_NAME_SIZE_VALIDATION_MESSAGE)
  private String lastName;
  @Email(message = ValidationMessages.EMAIL_VALIDATION_MESSAGE)
  private String email;
  private String userType;
  private MultipartFile profileImage;
}
