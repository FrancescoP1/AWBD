package com.fmi.eduhub.mapper;

import com.fmi.eduhub.dto.input.CourseInput;
import com.fmi.eduhub.dto.output.CourseItemOutput;
import com.fmi.eduhub.dto.output.CourseOutput;
import com.fmi.eduhub.dto.output.CourseSimpleOutput;
import com.fmi.eduhub.entity.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserEntityMapper.class, LessonEntityMapper.class})
public interface CourseEntityMapper {

    CourseEntityMapper INSTANCE = Mappers.getMapper(CourseEntityMapper.class);

    @Mapping(source = "courseName", target = "courseName")
    @Mapping(source = "courseDescription", target = "courseDescription")
    @Mapping(source = "courseCategory", target = "courseCategory")
    CourseEntity fromCourseRegistrationModelToEntity(CourseInput model);

    @Mapping(source = "courseId", target = "courseId")
    @Mapping(source = "courseName", target = "courseName")
    @Mapping(source = "courseDescription", target = "courseDescription")
    @Mapping(source = "courseCategory", target = "courseCategory")
    @Mapping(source = "courseStatus", target = "courseStatus")
    @Mapping(source = "author", target = "author")
    CourseItemOutput fromCourseEntityToItemOutput(CourseEntity entity);

    CourseOutput fromCourseEntityToOutput(CourseEntity courseEntity);

    CourseSimpleOutput fromCourseEntityToSimpleOutput(CourseEntity courseEntity);

    @Mapping(source = "courseId", target = "courseId")
    @Mapping(source = "courseName", target = "courseName")
    @Mapping(source = "courseCategory", target = "courseCategory")
    @Mapping(source = "courseDescription", target = "courseDescription")
    CourseEntity fromInputToEntity(CourseInput courseInput);

}
