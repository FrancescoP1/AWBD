package com.fmi.eduhub.entity;


import com.fmi.eduhub.authentication.jwtToken.JwtTokenEntity;
import com.fmi.eduhub.enums.UserRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private UUID userId;

    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters long")
    @Column(name = "first_name")
    private String firstName;

    @Size(min = 3, max = 30, message = "Last name must be between 2 and 30 characters long")
    @Column(name = "last_name")
    private String lastName;

    @Email(message = "You have entered an invalid email address")
    @Column(name = "email", unique = true)
    private String email;

    @Size(min = 6, max = 256)
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserRoleEnum userType;

    @OneToMany(mappedBy = "author", targetEntity = CourseEntity.class)
    private Set<CourseEntity> coursesCreated = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCourseEntity> coursesEnrolled = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLessonEntity> lessonsCompleted = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private JwtTokenEntity userToken;

    private String profilePictureKey;

    public void removeCourse(CourseEntity course) {
        for (UserCourseEntity userCourseEntity : coursesEnrolled) {
            if(userCourseEntity.getUser().equals(this)
                    && userCourseEntity.getCourse().equals(course)) {
                coursesEnrolled.remove(userCourseEntity);
                userCourseEntity.setCourse(null);
                userCourseEntity.setUser(null);
            }
        }
    }

    public void removeLesson(LessonEntity lesson) {
        for (UserLessonEntity userLessonEntity : lessonsCompleted) {
            if(userLessonEntity.getUser().equals(this) &&
                    userLessonEntity.getLesson().equals(lesson)) {
                lessonsCompleted.remove(userLessonEntity);
                userLessonEntity.setLesson(null);
                userLessonEntity.setUser(null);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(userType, that.userType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstName, lastName, email, userType);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
