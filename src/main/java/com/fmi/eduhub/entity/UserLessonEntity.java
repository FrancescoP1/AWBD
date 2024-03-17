package com.fmi.eduhub.entity;

import com.fmi.eduhub.entity.embedableTypes.UserLessonId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "userLesson")
@Getter
@Setter
@NoArgsConstructor
public class UserLessonEntity {

    @EmbeddedId
    private UserLessonId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id")
    private LessonEntity lesson;

    @Column(name = "is_completed")
    private boolean isCompleted;
    @Column(name = "date_completed")
    private OffsetDateTime dateCompleted;

    public UserLessonEntity (UserEntity user, LessonEntity lesson) {
        this.user = user;
        this.lesson = lesson;
        this.id = new UserLessonId(user.getUserId(), lesson.getLessonId());
        this.dateCompleted = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLessonEntity that = (UserLessonEntity) o;
        return Objects.equals(user, that.user) && Objects.equals(lesson, that.lesson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, lesson);
    }
}
