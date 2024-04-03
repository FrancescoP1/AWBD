package com.fmi.eduhub.mapper;

import com.fmi.eduhub.dto.input.LessonInputModel;
import com.fmi.eduhub.dto.output.LessonOutput;
import com.fmi.eduhub.entity.LessonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonEntityMapper {

  LessonEntityMapper INSTANCE = Mappers.getMapper(LessonEntityMapper.class);

  @Mapping(source = "lessonId", target = "lessonId")
  @Mapping(source = "lessonTitle", target = "lessonTitle")
  @Mapping(source = "courseId", target = "course.courseId")
  @Mapping(source = "lessonDescription", target = "lessonDescription")
  LessonEntity fromLessonInputToEntity(LessonInputModel lessonInputModel);

  @Mapping(source = "course.courseId", target = "courseId")
  LessonInputModel fromLessonEntityToModel(LessonEntity lessonEntity);

  @Mapping(source = "lessonVideo", target = "videoUrl")
  LessonOutput fromLessonEntityToOutput(LessonEntity lessonEntity);

  List<LessonOutput> fromLessonEntityToOutput(List<LessonEntity> lessonEntityList);
}
