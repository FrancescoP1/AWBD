package com.fmi.eduhub.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationModel {
    @NotBlank(message = "First name cannot be blank!")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank!")
    private String lastName;

    @Email(message = "Please enter a valid email address.")
    private String emailAddress;

    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
}
