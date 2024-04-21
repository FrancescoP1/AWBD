package com.fmi.eduhub.dto.input;

import com.fmi.eduhub.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class LessonInputModel {
  private String lessonId;
  @Size(min = 5, max = 100, message = ValidationMessages.LESSON_TITLE_SIZE_VALIDATION_MESSAGE)
  private String lessonTitle;
  @Size(min = 10, max = 2000, message = ValidationMessages.LESSON_DESCRIPTION_SIZE_VALIDATION_MESSAGE)
  private String lessonDescription;
  private String lessonVideo;
  @NotBlank(message = ValidationMessages.LESSON_COURSE_ID_NOT_BLANK_VALIDATION_MESSAGE)
  private String courseId;
  private MultipartFile thumbnailImage;
  private MultipartFile lessonPdf;
}
