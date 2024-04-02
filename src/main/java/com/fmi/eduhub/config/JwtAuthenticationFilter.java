package com.fmi.eduhub.config;

import com.fmi.eduhub.authentication.jwtToken.JwtTokenRepository;
import com.fmi.eduhub.exception.CustomJwtException;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.service.UsersEntityService;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;
    private final JwtTokenRepository jwtTokenRepository;
    private final UsersEntityService userDetailsService;

    @Resource(name = "handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        // extract header that contains bearer token
        final String authenticationHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;
        if(authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //skip "Bearer " when extracting the token
        jwtToken = authenticationHeader.substring(7);
        try {
            userEmail = jwtAuthService.extractUsername(jwtToken); // extract userEmail from token
            // email is present in the jwtToken and the user is not authenticated
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails =
                    this.userDetailsService.loadUserByUsername(userEmail);
                boolean isDbTokenValid =
                    jwtTokenRepository.existsByJwtTokenAndExpiredFalseAndRevokedFalse(jwtToken);
                if(!isDbTokenValid) {
                    throw new CustomJwtException(ExceptionConstants.TOKEN_EXPIRED);
                }
                if(jwtAuthService.isJwtTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (
            JwtException | IllegalArgumentException | AccessDeniedException exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }
}
