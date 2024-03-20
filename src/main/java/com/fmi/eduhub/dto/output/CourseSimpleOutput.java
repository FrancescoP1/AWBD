package com.fmi.eduhub.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSimpleOutput {
  private String courseId;
  private String courseName;
  private String courseCategory;
  private String thumbnailUrl;
}
