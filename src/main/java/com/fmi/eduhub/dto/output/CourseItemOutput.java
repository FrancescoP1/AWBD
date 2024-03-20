package com.fmi.eduhub.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseItemOutput {
    private String courseId;
    private String courseName;
    private String courseCategory;
    private String courseDescription;
    private String thumbnailUrl;
    private String courseStatus;
    private boolean isCompleted;
    private boolean canEditAndView = false;
    private boolean canView = false;
    private boolean canApprove = false;
    private boolean canEnroll = false;
    private Double rating;
    private UserSimpleOutput author;

}
