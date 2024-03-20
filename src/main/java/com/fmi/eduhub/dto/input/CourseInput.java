package com.fmi.eduhub.dto.input;

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
    @Size(min = 5, message = "Course name must be at least 5 characters long")
    private String courseName;
    @Size(min = 20, message = "Description must be at least 20 characters long.")
    private String courseDescription;
    private String courseCategory;
    private MultipartFile thumbnailImage;
}
