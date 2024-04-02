package com.fmi.eduhub.config;

import com.fmi.eduhub.authentication.LogOutService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${frontend.url}")
  private String allowedOrigin;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;

  @Resource(name = "handlerExceptionResolver")
  private final HandlerExceptionResolver handlerExceptionResolver;
  private final LogOutService logOutService;

  private static final String[] AUTH_WHITELIST = {
      "/api/courses/"
  };

  private static final String[] AUTH_ADMIN_LIST = {
      "/api/courses/*/approve",
      "/api/courses/reject",
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf().disable()
        .cors().and()
        .authorizeHttpRequests()
        .requestMatchers("/api/authentication/**")
        .permitAll()
        .requestMatchers(AUTH_WHITELIST).permitAll()
        .requestMatchers("/api/courses/createCourse").hasAnyAuthority("ROLE_ADMIN", "ROLE_AUTHOR")
        .requestMatchers(AUTH_ADMIN_LIST).hasAnyAuthority("ROLE_ADMIN")
        .anyRequest()
        .authenticated()
        .and().httpBasic()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout()
        .logoutUrl("/api/authentication/logout")
        .addLogoutHandler(logOutService)
        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()));
    return httpSecurity.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    ArrayList<String> allowedOrigins = new ArrayList<>();
    allowedOrigins.add(allowedOrigin);
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Auth", "Refresh"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);

    return source;
  }

}
