package com.fmi.eduhub.authentication;

import com.fmi.eduhub.authentication.jwtToken.JwtTokenEntity;
import com.fmi.eduhub.authentication.jwtToken.JwtTokenRepository;
import com.fmi.eduhub.config.JwtAuthService;
import com.fmi.eduhub.dto.UserModel;
import com.fmi.eduhub.dto.input.UserRegistrationModel;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.enums.UserRoleEnum;
import com.fmi.eduhub.exception.CustomJwtException;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.mapper.UserEntityMapper;
import com.fmi.eduhub.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final PasswordEncoder passwordEncoder;
  private final UserEntityRepository userEntityRepository;
  private final JwtAuthService jwtAuthService;
  private final AuthenticationManager authenticationManager;

  private final UserDetailsService userDetailsService;

  private final JwtTokenRepository jwtTokenRepository;
  private final UserEntityMapper userEntityMapper = UserEntityMapper.INSTANCE;

  @Transactional
  public ResponseEntity<UserModel> registerUser(UserRegistrationModel registrationModel) {
    UserEntity userToBeRegistered = userEntityMapper.fromUserRegistrationModelToEntity(registrationModel);
    if(userEntityRepository.existsByEmail(userToBeRegistered.getEmail())) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    userToBeRegistered.setUserType(UserRoleEnum.USER);
    userToBeRegistered.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
    userToBeRegistered = userEntityRepository.save(userToBeRegistered);
    UserModel registeredUser = userEntityMapper.fromUserEntityToModel(userToBeRegistered);
    return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
  }


  @Transactional
  public AuthenticationResponse loginUser(UserLogInModel userLogInModel) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userLogInModel.getEmail(),
            userLogInModel.getPassword()));
    Optional<UserEntity> userEntityOptional =
        userEntityRepository.findByEmail(userLogInModel.getEmail());

    if(userEntityOptional.isPresent()) {
      UserEntity userEntity = userEntityOptional.get();
      revokeUserToken(userEntity);
      HashMap<String, Object> extras = new HashMap<>();
      extras.put("role", userEntity.getUserType().toString());
      String jwtToken = jwtAuthService.generateJwtToken(extras, userEntity);
      String refreshToken = jwtAuthService.generateRefreshToken(userEntity);
      saveUserToken(userEntity, jwtToken, refreshToken);
      return
          AuthenticationResponse.builder().jwtToken(jwtToken).refreshToken(refreshToken).build();
    } else {
      throw new UsernameNotFoundException(ExceptionConstants.USERNAME_NOT_FOUND);
    }

  }

  public AuthenticationResponse refreshUserToken(
      String jwtAuthToken,
      String jwtRefreshToken) {
    if(jwtAuthToken == null || !jwtAuthToken.startsWith("Bearer ") || !jwtRefreshToken.startsWith("Bearer ")) {
      throw new CustomJwtException(ExceptionConstants.MISSING_HEADERS);
    }
    String jwtToken = jwtAuthToken.substring("Bearer ".length());
    String refreshToken = jwtRefreshToken.substring("Bearer ".length());
    if(!jwtTokenRepository.existsByJwtTokenAndRefreshToken(jwtToken, refreshToken)) {
      throw new CustomJwtException(ExceptionConstants.INVALID_TOKEN);
    }
    String userEmail = jwtAuthService.extractUsername(refreshToken);
    if(userEmail != null) {
      Optional<UserEntity> userEntityOptional = userEntityRepository.findByEmail(userEmail);
      if(userEntityOptional.isEmpty()) {
        throw new UsernameNotFoundException(ExceptionConstants.USERNAME_NOT_FOUND);
      }
      UserEntity userEntity = userEntityOptional.get();
      if(jwtAuthService.isJwtTokenValid(refreshToken, userEntity)) {
        revokeUserToken(userEntity);

        HashMap<String, Object> extras = new HashMap<>();
        extras.put("role", userEntity.getUserType().toString());

        String newJwtToken = jwtAuthService.generateJwtToken(extras, userEntity);
        saveUserToken(userEntity, newJwtToken, refreshToken);
        return AuthenticationResponse
                .builder()
                .jwtToken(newJwtToken)
                .refreshToken(refreshToken)
                .build();
      }
    }
    throw new CustomJwtException("Something went wrong!");
  }

  @Transactional
  public void saveUserToken(UserEntity userEntity, String jwtToken, String refreshToken) {
    Optional<JwtTokenEntity> userToken = jwtTokenRepository.findByUserEmail(userEntity.getEmail());
    Integer tokenId = null;
    if(userToken.isPresent()) {
      tokenId = userToken.get().getId();
    }
    JwtTokenEntity jwtTokenEntity =
        JwtTokenEntity.builder()
            .user(userEntity)
            .jwtToken(jwtToken)
            .refreshToken(refreshToken)
            .expired(false)
            .revoked(false)
            .build();
    if(tokenId != null) {
      jwtTokenEntity.setId(tokenId);
    }
    jwtTokenRepository.save(jwtTokenEntity);
  }

  @Transactional
  public void revokeUserToken(UserEntity userEntity) {
    Optional<JwtTokenEntity> userToken =
        jwtTokenRepository.findByUserEmail(userEntity.getEmail());
    userToken.ifPresent(jwtTokenRepository::delete);
  }

  public boolean isLoggedIn(Authentication authentication) {
    if(authentication != null) {
      return authentication.isAuthenticated();
    }
    return false;
  }
}
