package com.fmi.eduhub.entity.embedableTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCourseId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "course_id")
    private UUID courseId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if(obj == null || this.getClass() != obj.getClass())
            return false;

        UserCourseId that = (UserCourseId) obj;
        return this.userId.equals(that.userId) &&
                this.courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, courseId);
    }
}
