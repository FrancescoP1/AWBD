package com.fmi.eduhub.authentication;

import com.fmi.eduhub.authentication.jwtToken.JwtTokenEntity;
import com.fmi.eduhub.authentication.jwtToken.JwtTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogOutService implements LogoutHandler {
  private final JwtTokenRepository jwtTokenRepository;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    String authorizationHeader = request.getHeader("Authorization");
    if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return;
    }
    // skip Bearer...
    String jwtToken = authorizationHeader.substring("Bearer ".length());

    Optional<JwtTokenEntity> jwtTokenEntityOptional = jwtTokenRepository.findByJwtToken(jwtToken);
    if(jwtTokenEntityOptional.isPresent()) {
      JwtTokenEntity jwtTokenEntity = jwtTokenEntityOptional.get();
      jwtTokenEntity.setExpired(true);
      jwtTokenEntity.setRevoked(true);
      jwtTokenRepository.save(jwtTokenEntity);
      SecurityContextHolder.clearContext();
    }
  }
}
