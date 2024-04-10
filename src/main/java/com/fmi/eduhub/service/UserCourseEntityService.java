package com.fmi.eduhub.service;

import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.LessonEntity;
import com.fmi.eduhub.entity.UserCourseEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.entity.embedableTypes.UserCourseId;
import com.fmi.eduhub.enums.CourseStatusEnum;
import com.fmi.eduhub.repository.UserCourseEntityRepository;
import com.fmi.eduhub.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCourseEntityService {
  private final UserCourseEntityRepository userCourseEntityRepository;
  private final UserLessonEntityService userLessonEntityService;

  private final UserEntityRepository userEntityRepository;

  public boolean enrollIntoCourse(UserEntity userEntity, CourseEntity courseEntity) {
    if(!isUserEnrolledIntoCourse(userEntity.getUserId(), courseEntity.getCourseId())
        && courseEntity.getCourseStatus() == CourseStatusEnum.APPROVED) {
      UserCourseEntity userCourseEntity =
          new UserCourseEntity(userEntity, courseEntity);
      userCourseEntityRepository.save(userCourseEntity);
      userEntity.getCoursesEnrolled().add(userCourseEntity);
      userEntityRepository.save(userEntity);
      return true;
    }
    return false;
  }

  public boolean hasUserCompletedCourse(UserEntity userEntity, CourseEntity courseEntity) {
    Set<LessonEntity> courseLessons = courseEntity.getLessons();
    if(!isUserEnrolledIntoCourse(userEntity.getUserId(), courseEntity.getCourseId())) {
      return false;
    }
    UserCourseId userCourseId =
        new UserCourseId(userEntity.getUserId(), courseEntity.getCourseId());
    UserCourseEntity userCourseEntity = userCourseEntityRepository.getReferenceById(userCourseId);
    if(userCourseEntity.isCompleted()) {
      return true;
    }

    // check to see if all lessons of the course are completed
    for(LessonEntity lesson: courseLessons) {
      if(!userLessonEntityService
          .isLessonCompleted(userEntity.getUserId(), lesson.getLessonId())) {
        return false;
      }
    }

    // set course as completed.
    userCourseEntity.setCompleted(true);
    userCourseEntityRepository.save(userCourseEntity);
    return true;
  }

  Page<CourseEntity> getCoursesEnrolled(UserEntity user, Pageable pageable) {
    Page<UserCourseEntity> userCourseEntityPage =
        userCourseEntityRepository
            .findAllByUserAndCourseCourseStatus(user, CourseStatusEnum.APPROVED, pageable);
    return  userCourseEntityPage.map(this::fromUserCourseEntityToCourseEntity);
  }

  Page<CourseEntity> getCoursesCompleted(UserEntity user, Pageable pageable) {

    Page<UserCourseEntity> userCourseEntityPage =
        userCourseEntityRepository
            .findAllByUserAndIsCompletedAndCourseCourseStatus(
                user, true, CourseStatusEnum.APPROVED, pageable);
    System.out.println("after query");
    return userCourseEntityPage.map(this::fromUserCourseEntityToCourseEntity);
  }

  public CourseEntity fromUserCourseEntityToCourseEntity(UserCourseEntity entity) {
    return entity.getCourse();
  }

  public boolean isUserEnrolledIntoCourse(UUID userId, UUID courseId) {
    UserCourseId userCourseId = new UserCourseId(userId, courseId);
    return userCourseEntityRepository.existsById(userCourseId);
  }

}
