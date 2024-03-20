package com.fmi.eduhub.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewInput {
  private String reviewId;
  private String comment;
  private int rating;
  private String courseId;
}
