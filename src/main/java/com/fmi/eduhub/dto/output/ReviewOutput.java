package com.fmi.eduhub.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewOutput {
  private String reviewId;
  private CourseSimpleOutput course;
  private int rating;
  private String comment;
  private UserSimpleOutput reviewer;

  private boolean approved = false;
  private boolean canApprove = false;
  private boolean canDelete = false;
  private boolean canView = false;
}
