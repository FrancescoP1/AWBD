package com.fmi.eduhub.authentication.jwtToken;

import com.fmi.eduhub.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tokens")
public class JwtTokenEntity {
  @Id
  @GeneratedValue
  private Integer id;

  @Column(unique = true)
  private String jwtToken;

  @Column(unique = true)
  private String refreshToken;

  @Column(name = "is_expired")
  private boolean expired;

  @Column(name = "is_revoked")
  private boolean revoked;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;
}
