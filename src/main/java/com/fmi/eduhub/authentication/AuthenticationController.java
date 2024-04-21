package com.fmi.eduhub.authentication;

import com.fmi.eduhub.dto.UserModel;
import com.fmi.eduhub.dto.input.UserRegistrationModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/authentication")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping(value = "/register")
  public ResponseEntity<UserModel> registerUser(@RequestBody @Valid UserRegistrationModel userRegistrationModel) {
    return authenticationService.registerUser(userRegistrationModel);
  }

  @PostMapping(value = "/login")
  public ResponseEntity<AuthenticationResponse> logIn(
      @RequestBody UserLogInModel userLogInModel) {
    AuthenticationResponse authenticationResponse =
        authenticationService.loginUser(userLogInModel);
    return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/refreshToken")
  public ResponseEntity<AuthenticationResponse> refreshToken(
      @RequestHeader(value = "Auth") String authHeader,
      @RequestHeader(value = "Refresh") String refreshHeader) {
    AuthenticationResponse authenticationResponse = authenticationService.refreshUserToken(authHeader, refreshHeader);
    return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
  }

  @GetMapping("/isLoggedIn")
  public ResponseEntity<Boolean> isLoggedIn(Authentication authentication) {
    //System.out.println("userName: " + authentication.getName());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(authenticationService.isLoggedIn(authentication));
  }
}
