package com.fmi.eduhub.dto.output;

import com.fmi.eduhub.dto.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseOutput {
  private String courseId;
  private String courseName;
  private String courseCategory;
  private String courseDescription;
  private String thumbnailUrl;
  private String courseStatus;
  private UserModel author;
  private List<LessonOutput> lessons;
}