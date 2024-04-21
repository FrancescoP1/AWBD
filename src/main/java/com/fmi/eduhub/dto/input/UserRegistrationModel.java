package com.fmi.eduhub.dto.input;

import com.fmi.eduhub.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationModel {
    @Size(min = 2, max = 100, message = ValidationMessages.FIRST_NAME_SIZE_VALIDATION_MESSAGE)
    private String firstName;
    @Size(min = 2, max = 100, message = ValidationMessages.LAST_NAME_SIZE_VALIDATION_MESSAGE)
    private String lastName;

    @Email(message = ValidationMessages.EMAIL_VALIDATION_MESSAGE)
    private String emailAddress;

    @Size(min = 6, max = 100, message = ValidationMessages.PASSWORD_SIZE_VALIDATION_MESSAGE)
    private String password;
    @Size(min = 6, max = 100, message = ValidationMessages.PASSWORD_SIZE_VALIDATION_MESSAGE)
    private String confirmPassword;
}
