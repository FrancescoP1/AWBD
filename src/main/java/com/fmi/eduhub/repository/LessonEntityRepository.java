package com.fmi.eduhub.repository;

import com.fmi.eduhub.entity.LessonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LessonEntityRepository extends JpaRepository<LessonEntity, UUID> {
  Page<LessonEntity> findAllByCourseCourseId(UUID courseId, Pageable pageable);
}
