package com.fmi.eduhub.dto.input;

import com.fmi.eduhub.validation.ValidationMessages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class ReviewInput {
  private String reviewId;
  @Size(min = 10, max = 1000, message = ValidationMessages.REVIEW_COMMENT_SIZE_VALIDATION_MESSAGE)
  private String comment;
  @Range(min = 1, max = 5, message = ValidationMessages.REVIEW_RATING_RANGE_VALIDATION_MESSAGE)
  private int rating;
  @NotBlank(message = ValidationMessages.REVIEW_COURSE_ID_NOT_BLANK_VALIDATION_MESSAGE)
  private String courseId;
}
