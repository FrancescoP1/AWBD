package com.fmi.eduhub.dto.input;

import com.fmi.eduhub.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectCourseInput {
  @NotBlank(message = ValidationMessages.REJECT_COURSE_ID_NOT_BLANK_VALIDATION_MESSAGE)
  String courseId;
  @Size(message = ValidationMessages.REJECT_COURSE_MESSAGE_SIZE_VALIDATION_MESSAGE)
  String rejectionMessage;
}
