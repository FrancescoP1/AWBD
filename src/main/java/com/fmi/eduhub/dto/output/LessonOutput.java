package com.fmi.eduhub.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonOutput {
  private String lessonId;
  private String lessonTitle;
  private String lessonDescription;
  private String thumbnailUrl;
  private String videoUrl;
  private String pdfUrl;
  private CourseSimpleOutput courseOutput;
  private boolean canEdit = false;
  private boolean canView = false;
  private boolean isCompleted = false;
  private boolean canComplete = false;
}
