package com.fmi.eduhub.repository;

import com.fmi.eduhub.entity.UserCourseEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.entity.embedableTypes.UserCourseId;
import com.fmi.eduhub.enums.CourseStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseEntityRepository extends JpaRepository<UserCourseEntity, UserCourseId> {

  Page<UserCourseEntity> findAllByUserAndIsCompletedAndCourseCourseStatus(
      UserEntity user, boolean isCompleted, CourseStatusEnum courseStatus, Pageable pageable);
  Page<UserCourseEntity> findAllByUserAndCourseCourseStatus(
      UserEntity user, CourseStatusEnum courseStatus, Pageable pageable);

}
