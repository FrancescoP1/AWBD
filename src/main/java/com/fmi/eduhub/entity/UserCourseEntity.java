package com.fmi.eduhub.entity;

import com.fmi.eduhub.entity.embedableTypes.UserCourseId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user_course")
@Getter
@Setter
@NoArgsConstructor
public class UserCourseEntity {

    @EmbeddedId
    private UserCourseId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId(value = "userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId(value = "courseId")
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @Column(name = "is_completed")
    private boolean isCompleted;

    public UserCourseEntity(UserEntity user, CourseEntity course) {
        this.course = course;
        this.user = user;
        this.id = new UserCourseId(user.getUserId(), course.getCourseId());
        isCompleted = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCourseEntity that = (UserCourseEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, course);
    }
}
