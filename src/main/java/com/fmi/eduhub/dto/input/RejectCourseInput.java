package com.fmi.eduhub.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectCourseInput {
  String courseId;
  String rejectionMessage;
}
