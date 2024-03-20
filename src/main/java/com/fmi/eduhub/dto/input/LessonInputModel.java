package com.fmi.eduhub.dto.input;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class LessonInputModel {
  private String lessonId;
  private String lessonTitle;
  private String lessonDescription;
  private String lessonVideo;
  private String courseId;
  private MultipartFile thumbnailImage;
  private MultipartFile lessonPdf;
}
