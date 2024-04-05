package com.fmi.eduhub.mapper;

import com.fmi.eduhub.dto.input.ReviewInput;
import com.fmi.eduhub.dto.output.ReviewOutput;
import com.fmi.eduhub.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {UserEntityMapper.class, CourseEntityMapper.class})
public interface ReviewEntityMapper {

  ReviewEntityMapper INSTANCE =
      Mappers.getMapper(ReviewEntityMapper.class);

  @Mapping(source = "courseId", target = "course.courseId")
  ReviewEntity fromInputToEntity(ReviewInput reviewInput);

  ReviewOutput fromEntityToOutput(ReviewEntity reviewEntity);


}
