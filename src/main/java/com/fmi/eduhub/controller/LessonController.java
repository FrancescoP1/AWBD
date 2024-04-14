package com.fmi.eduhub.controller;

import com.fmi.eduhub.service.LessonEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/lesson")
public class LessonController {
  private final LessonEntityService lessonEntityService;

  @PostMapping(value = "/{lessonId}/complete")
  public ResponseEntity<Boolean> completeLesson(
      @PathVariable(name = "lessonId") String lessonId) {
    return new ResponseEntity<>(
        lessonEntityService.completeLesson(lessonId),
        HttpStatus.OK);
  }
}
