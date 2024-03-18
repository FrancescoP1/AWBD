package com.fmi.eduhub.repository;

import com.fmi.eduhub.entity.UserLessonEntity;
import com.fmi.eduhub.entity.embedableTypes.UserLessonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserLessonEntityRepository extends JpaRepository<UserLessonEntity, UserLessonId> {
  int countAllByUserUserIdAndLessonCourseCourseId(UUID userId, UUID courseId);
}
