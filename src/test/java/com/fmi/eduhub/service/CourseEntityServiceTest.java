package com.fmi.eduhub.service;

import com.fmi.eduhub.repository.CourseEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseEntityServiceTest {
  @Mock private CourseEntityRepository courseEntityRepositoryMock;
  @InjectMocks private CourseEntityService courseEntityService;
  private final String courseId = UUID.randomUUID().toString();

  @Test
  @DisplayName("deleteCourseTest_courseNotFound")
  public void deleteCourseTest_courseNotFound() throws AccessDeniedException {
    when(courseEntityRepositoryMock.findById(any()))
        .thenReturn(Optional.empty());
    boolean shouldBeFalse = courseEntityService.deleteCourse(courseId);
    assertAll(
        () -> assertThat(shouldBeFalse).isFalse(),
        () -> verify(courseEntityRepositoryMock, times(1))
            .findById(UUID.fromString(courseId))
    );
  }
}
