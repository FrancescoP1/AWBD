package com.fmi.eduhub.dto.input;

import com.fmi.eduhub.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseInput {
    private String courseId;
    @Size(min = 5, message = ValidationMessages.COURSE_NAME_SIZE_VALIDATION_MESSAGE)
    private String courseName;
    @Size(min = 20, message = ValidationMessages.COURSE_DESCRIPTION_SIZE_VALIDATION_MESSAGE)
    private String courseDescription;
    @NotBlank(message = ValidationMessages.COURSE_CATEGORY_NOT_BLANK_VALIDATION_MESSAGE)
    private String courseCategory;
    private MultipartFile thumbnailImage;
}
