package com.fmi.eduhub.repository;

import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.ReviewEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.enums.ReviewStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewEntityRepository extends JpaRepository<ReviewEntity, UUID> {
  Page<ReviewEntity> findAllByCourseCourseIdAndReviewStatus(
      UUID courseId, ReviewStatusEnum reviewStatus, Pageable pageable);

  Page <ReviewEntity> findAllByCourseCourseId(UUID courseId, Pageable pageable);
  Page <ReviewEntity> findAllByReviewStatus(ReviewStatusEnum reviewStatusEnum, Pageable pageable);

  @Query("SELECT AVG(r.rating) FROM ReviewEntity r " +
      "WHERE r.reviewStatus = 'APPROVED' AND r.course.courseId = :courseId")
  Double findAvgRatingByCourse(@Param("courseId") UUID courseId);

  boolean existsByReviewerAndCourse(UserEntity reviewer, CourseEntity course);

}
