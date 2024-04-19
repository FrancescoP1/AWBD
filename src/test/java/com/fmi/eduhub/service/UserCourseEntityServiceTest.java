package com.fmi.eduhub.service;

import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.LessonEntity;
import com.fmi.eduhub.entity.UserCourseEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.entity.embedableTypes.UserCourseId;
import com.fmi.eduhub.enums.CourseStatusEnum;
import com.fmi.eduhub.repository.UserCourseEntityRepository;
import com.fmi.eduhub.repository.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserCourseEntityServiceTest {

    @Mock
    private UserCourseEntityRepository userCourseEntityRepository;
    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private UserLessonEntityService userLessonEntityService;
    @InjectMocks
    private UserCourseEntityService userCourseEntityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnrollIntoCourse_Success() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        CourseEntity course = new CourseEntity();
        course.setCourseId(courseId);
        course.setCourseStatus(CourseStatusEnum.APPROVED);

        when(userCourseEntityRepository.existsById(any(UserCourseId.class))).thenReturn(false);
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(user);

        boolean result = userCourseEntityService.enrollIntoCourse(user, course);

        assertTrue(result);
        verify(userCourseEntityRepository).save(any(UserCourseEntity.class));
    }

    @Test
    void testEnrollIntoCourse_FailureAlreadyEnrolled() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        CourseEntity course = new CourseEntity();
        course.setCourseId(courseId);
        course.setCourseStatus(CourseStatusEnum.APPROVED);

        when(userCourseEntityRepository.existsById(any(UserCourseId.class))).thenReturn(true);

        boolean result = userCourseEntityService.enrollIntoCourse(user, course);

        assertFalse(result);
        verify(userCourseEntityRepository, never()).save(any(UserCourseEntity.class));
    }

    @Test
    void testHasUserCompletedCourse_NotEnrolled_ReturnsFalse() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        CourseEntity course = new CourseEntity();
        course.setCourseId(courseId);
        Set<LessonEntity> lessons = new HashSet<>();
        course.setLessons(lessons);

        when(userCourseEntityRepository.existsById(any(UserCourseId.class))).thenReturn(false);

        assertFalse(userCourseEntityService.hasUserCompletedCourse(user, course));
    }

    @Test
    void testHasUserCompletedCourse_AllLessonsCompleted_ReturnsTrue() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        CourseEntity course = new CourseEntity();
        course.setCourseId(courseId);
        Set<LessonEntity> lessons = new HashSet<>();
        LessonEntity lesson = new LessonEntity();
        lesson.setLessonId(UUID.randomUUID());
        lessons.add(lesson);
        course.setLessons(lessons);

        UserCourseEntity userCourseEntity = new UserCourseEntity(user, course);
        userCourseEntity.setCompleted(false);

        when(userCourseEntityRepository.existsById(any(UserCourseId.class))).thenReturn(true);
        when(userCourseEntityRepository.getReferenceById(any(UserCourseId.class))).thenReturn(userCourseEntity);
        when(userLessonEntityService.isLessonCompleted(userId, lesson.getLessonId())).thenReturn(true);

        assertTrue(userCourseEntityService.hasUserCompletedCourse(user, course));
        verify(userCourseEntityRepository).save(userCourseEntity);
    }

    @Test
    void getCoursesEnrolled_Success() {
        UserEntity user = new UserEntity();
        user.setUserId(UUID.randomUUID());
        Pageable pageable = Pageable.unpaged();

        userCourseEntityService.getCoursesEnrolled(user, pageable);

        verify(userCourseEntityRepository).findAllByUserAndCourseCourseStatus(eq(user), eq(CourseStatusEnum.APPROVED), eq(pageable));
    }

}
