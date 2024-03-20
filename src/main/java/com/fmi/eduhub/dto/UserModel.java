package com.fmi.eduhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userType;
}
