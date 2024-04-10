package com.fmi.eduhub.service;

import com.fmi.eduhub.entity.LessonEntity;
import com.fmi.eduhub.entity.UserCourseEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.entity.UserLessonEntity;
import com.fmi.eduhub.entity.embedableTypes.UserCourseId;
import com.fmi.eduhub.entity.embedableTypes.UserLessonId;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.exception.ResourceNotAccessibleException;
import com.fmi.eduhub.repository.UserCourseEntityRepository;
import com.fmi.eduhub.repository.UserEntityRepository;
import com.fmi.eduhub.repository.UserLessonEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLessonEntityService {
  private final UserLessonEntityRepository userLessonEntityRepository;
  private final UserEntityRepository userEntityRepository;
  private final UserCourseEntityRepository userCourseEntityRepository;

  @Transactional
  public boolean completeLesson(UserEntity user, LessonEntity lesson) {
    if(isLessonCompleted(user.getUserId(), lesson.getLessonId())) {
      throw new ResourceNotAccessibleException(ExceptionConstants.LESSON_ALREADY_COMPLETED);
    }
    boolean isUserEnrolledInCourse =
        isUserEnrolledIntoCourse(user.getUserId(), lesson.getCourse().getCourseId());
    if(isUserEnrolledInCourse) {
      UserLessonEntity userLessonEntity =
          new UserLessonEntity(user, lesson);
      user.getLessonsCompleted().add(userLessonEntity);
      userLessonEntityRepository.save(userLessonEntity);
      userEntityRepository.save(user);
      // check to see if the whole course has been completed
      UserCourseId userCourseId = new UserCourseId(user.getUserId(), lesson.getCourse().getCourseId());
      UserCourseEntity userCourseEntity =
          userCourseEntityRepository.getReferenceById(userCourseId);
      int courseLessonsCompleted = userLessonEntityRepository
          .countAllByUserUserIdAndLessonCourseCourseId(user.getUserId(), lesson.getCourse().getCourseId());
      if( courseLessonsCompleted == lesson.getCourse().getLessons().size()) {
        userCourseEntity.setCompleted(true);
        userCourseEntityRepository.save(userCourseEntity);
      }
      return true;
    }
    return false;
  }

  public boolean isLessonCompleted(UUID userId, UUID lessonId) {
    UserLessonId userLessonId = new UserLessonId(userId, lessonId);
    return userLessonEntityRepository.existsById(userLessonId);
  }

  public boolean isUserEnrolledIntoCourse(UUID userId, UUID courseId) {
    UserCourseId userCourseId = new UserCourseId(userId, courseId);
    return userCourseEntityRepository.existsById(userCourseId);
  }
}
