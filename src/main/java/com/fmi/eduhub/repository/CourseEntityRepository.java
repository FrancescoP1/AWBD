package com.fmi.eduhub.repository;

import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.enums.CourseCategoryEnum;
import com.fmi.eduhub.enums.CourseStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseEntityRepository extends JpaRepository<CourseEntity, UUID> {
  Page<CourseEntity> findAllByCourseCategory(CourseCategoryEnum courseCategoryEnum, Pageable pageable);
  Page<CourseEntity> findAllByCourseCategoryIsInAndCourseStatus(
      List<CourseCategoryEnum> courseCategories, CourseStatusEnum courseStatus, Pageable pageable);
  Page<CourseEntity> findAllByCourseStatus(CourseStatusEnum courseStatusEnum, Pageable pageable);

  Page<CourseEntity> findAllByCourseStatusIn(List<CourseStatusEnum> courseStatuses, Pageable pageable);
  Page<CourseEntity> findAllByCourseCategoryAndCourseStatus(
      CourseCategoryEnum courseCategory, CourseStatusEnum courseStatus, Pageable pageable);
  Page<CourseEntity> findAllByAuthorUserIdOrCourseStatus(
      UUID authorId, CourseStatusEnum courseStatusEnum, Pageable pageable);

  Page<CourseEntity> findAllByCourseCategoryAndAuthorUserIdOrCourseCategoryAndCourseStatus(
   CourseCategoryEnum courseCategory,
   UUID authorId,
   CourseCategoryEnum courseCategory2,
   CourseStatusEnum  courseStatus,
   Pageable pageable);

  Page<CourseEntity> findAllByAuthorUserId(UUID userId, Pageable pageable);
}
