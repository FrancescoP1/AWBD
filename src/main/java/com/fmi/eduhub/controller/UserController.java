package com.fmi.eduhub.controller;

import com.fmi.eduhub.dto.UserModel;
import com.fmi.eduhub.dto.input.UserProfileInput;
import com.fmi.eduhub.dto.output.UserProfileOutput;
import com.fmi.eduhub.service.UsersEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UsersEntityService usersEntityService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> getUserById(@PathVariable(name = "userId") String userId) {
        return usersEntityService.findUserById(userId);
    }

    @PutMapping("/updatePicture")
    public ResponseEntity<Boolean> updateProfilePicture(
        @ModelAttribute(name = "newImage") MultipartFile newImage) {
        return new ResponseEntity<>(
            usersEntityService.updateProfilePicture(newImage),
            HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileOutput> getUserProfile() {
        return new ResponseEntity<>(
            usersEntityService.getUserProfile(),
            HttpStatus.OK);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<Boolean> updateUserProfile(
        @ModelAttribute UserProfileInput userProfileInput) {
        return new ResponseEntity<>(
            usersEntityService.updateUserProfile(userProfileInput),
            HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<Boolean> deleteUser(
        @PathVariable(name = "userId") String userId) {
        return new ResponseEntity<>(
            usersEntityService.deleteUser(userId),
            HttpStatus.OK);
    }

    /*
    @GetMapping("/insertUser")
    public void insertDataH2() {
        usersEntityService.insertDataH2();
    }
     */
}
