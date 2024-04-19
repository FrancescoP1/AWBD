package com.fmi.eduhub.service;

import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.LessonEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.entity.embedableTypes.UserCourseId;
import com.fmi.eduhub.entity.embedableTypes.UserLessonId;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.exception.ResourceNotAccessibleException;
import com.fmi.eduhub.repository.UserCourseEntityRepository;
import com.fmi.eduhub.repository.UserEntityRepository;
import com.fmi.eduhub.repository.UserLessonEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLessonEntityServiceTest {

  private UserLessonEntityRepository userLessonEntityRepository;
  private UserEntityRepository userEntityRepository;
  private UserCourseEntityRepository userCourseEntityRepository;

  private UserLessonEntityService userLessonEntityService;

  private final static UUID userId = UUID.randomUUID();
  private final static UUID courseId = UUID.randomUUID();
  private final static UUID lessonId = UUID.randomUUID();

  private final static LessonEntity lessonEntity = new LessonEntity();
  private final static CourseEntity courseEntity = new CourseEntity();
  private final static UserEntity userEntity = new UserEntity();

  @BeforeEach
  public void setup() {
    userLessonEntityRepository = mock(UserLessonEntityRepository.class);
    userEntityRepository = mock(UserEntityRepository.class);
    userCourseEntityRepository = mock(UserCourseEntityRepository.class);
    userLessonEntityService =
        spy(new UserLessonEntityService(userLessonEntityRepository, userEntityRepository, userCourseEntityRepository));
    courseEntity.setCourseId(courseId);
    lessonEntity.setLessonId(lessonId);
    lessonEntity.setCourse(courseEntity);
    userEntity.setUserId(userId);
  }

  //@Test
  public void shouldCompleteLessonAndCourse() {
    // given
    doReturn(false).when(userLessonEntityService).isLessonCompleted(userId, lessonId);
    doReturn(true).when(userLessonEntityService).isUserEnrolledIntoCourse(userId, courseId);
    doNothing().when(userEntityRepository).save(userEntity);
    doNothing().when(userLessonEntityRepository).save(any());

  }

  @Test
  public void shouldNotCompleteLessonNotEnrolledIntoCourseTest() {
    // given
    doReturn(false).when(userLessonEntityService).isLessonCompleted(userId, lessonId);
    doReturn(false).when(userLessonEntityService).isUserEnrolledIntoCourse(userId, courseId);
    // when
    boolean shouldBeFalse = userLessonEntityService.completeLesson(userEntity, lessonEntity);
    // then
    assertThat(shouldBeFalse).isFalse();
  }

  @Test
  public void completeLessonShouldThrowExceptionWhenAlreadyCompleted() {
    // given
    doReturn(true).when(userLessonEntityService).isLessonCompleted(userId, lessonId);
    // when
    ResourceNotAccessibleException exception =
        assertThrows(ResourceNotAccessibleException.class,
            () -> userLessonEntityService.completeLesson(userEntity, lessonEntity));
    // then
    assertThat(exception).isNotNull();
    assertThat(exception.getMessage())
        .isNotNull().isEqualTo(ExceptionConstants.LESSON_ALREADY_COMPLETED);
  }

  @Test
  public void isUserEnrolledIntoCourseTest() {
    // given
    UserCourseId userCourseId = new UserCourseId(userId, courseId);
    when(userCourseEntityRepository.existsById(userCourseId)).thenReturn(true);
    // when
    boolean shouldBeTrue = userLessonEntityService.isUserEnrolledIntoCourse(userId, courseId);
    // then
    assertThat(shouldBeTrue).isTrue();
    verify(userCourseEntityRepository, times(1)).existsById(userCourseId);
  }

  @Test
  public void isLessonCompletedTest() {
    // given
    UserLessonId userLessonId = new UserLessonId(userId, lessonId);
    when(userLessonEntityRepository.existsById(userLessonId)).thenReturn(true);
    // when
    boolean shouldBeTrue = userLessonEntityService.isLessonCompleted(userId, lessonId);
    // then
    assertThat(shouldBeTrue).isTrue();
    verify(userLessonEntityRepository, times(1)).existsById(userLessonId);
  }
}
