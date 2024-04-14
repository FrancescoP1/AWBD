package com.fmi.eduhub.controller;

import com.fmi.eduhub.dto.input.CourseInput;
import com.fmi.eduhub.dto.input.LessonInputModel;
import com.fmi.eduhub.dto.input.RejectCourseInput;
import com.fmi.eduhub.dto.output.CourseItemOutput;
import com.fmi.eduhub.dto.output.CourseOutput;
import com.fmi.eduhub.dto.output.LessonOutput;
import com.fmi.eduhub.dto.output.ReviewOutput;
import com.fmi.eduhub.service.CourseEntityService;
import com.fmi.eduhub.service.UserCourseEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/courses")
public class CourseController {
    private final CourseEntityService courseEntityService;
    private final UserCourseEntityService userCourseEntityService;

    @PostMapping(value = "/")
    public ResponseEntity<CourseItemOutput> createCourse(
        @ModelAttribute CourseInput model) {
        System.out.println(model.getCourseCategory());
        return courseEntityService.createCourse(model);
    }

    @DeleteMapping(value = "/{courseId}")
    public boolean deleteCourse(
        @PathVariable(name = "courseId") String courseId)
        throws AccessDeniedException {
        return courseEntityService.deleteCourse(courseId);
    }

    @PutMapping(value="/")
    public ResponseEntity<CourseItemOutput> updateCourse(
        @ModelAttribute CourseInput courseInput) throws AccessDeniedException {
        return new ResponseEntity<>(
            courseEntityService.updateCourse(courseInput),
            HttpStatus.OK);
    }

    @PostMapping(value = "/addLesson")
    public ResponseEntity<Boolean> addLessonToCourse(
        @ModelAttribute LessonInputModel lessonInputModel) {
        return new ResponseEntity<>(
            courseEntityService.addLessonToCourse(lessonInputModel),
            HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{courseId}/removeLesson/{lessonId}")
    public ResponseEntity<Boolean> removeLessonFromCourse(
        @PathVariable(name = "courseId") String courseId,
        @PathVariable(name = "lessonId") String lessonId) {
        return new ResponseEntity<>(
            courseEntityService.removeLessonFromCourse(courseId, lessonId),
            HttpStatus.OK);
    }

    @PutMapping(value = "/editCourseLesson")
    public ResponseEntity<Boolean> editCourseLesson(
        @ModelAttribute LessonInputModel lessonInputModel) {
        return new ResponseEntity<>(
            courseEntityService.editCourseLesson(lessonInputModel),
            HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public ResponseEntity<Page<CourseItemOutput>> getCourses(
        Pageable pageable,
        @RequestParam(name = "courseCategory", required = false) String courseCategory) {
        if(courseCategory == null || courseCategory.equalsIgnoreCase("all")) {
            return new ResponseEntity<>(
                courseEntityService.getCourses(pageable),
                HttpStatus.OK);
        } else if (courseCategory.equals("admin")) {
            return new ResponseEntity<>(
                courseEntityService.getPendingCourses(pageable),
                HttpStatus.OK);
        } else if (courseCategory.equals("author")) {
            return new ResponseEntity<>(
                courseEntityService.getCoursesByAuthor(pageable),
                HttpStatus.OK);
        } else if (courseCategory.equals("completed")) {
            return new ResponseEntity<>(
                courseEntityService.getCoursesCompleted(pageable),
                HttpStatus.OK);
        } else if (courseCategory.equals("enrolled")) {
            return new ResponseEntity<>(
                courseEntityService.getCoursesEnrolled(pageable),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                courseEntityService.getCoursesByCategory(pageable, courseCategory),
                HttpStatus.OK);
        }
    }



    @GetMapping(value = "/{courseId}")
    public ResponseEntity<CourseItemOutput> getCourse(
        @PathVariable(name = "courseId") String courseId) {
        return  new ResponseEntity<>(
            courseEntityService.getCourse(courseId),
            HttpStatus.OK);
    }

    @GetMapping(value = "/{courseId}/lessons")
    public ResponseEntity<Page<LessonOutput>> getCourseLessons(
        @PathVariable(name = "courseId") String courseId,
        Pageable pageable) {
        return new ResponseEntity<>(
            courseEntityService.getCourseLessons(courseId, pageable),
            HttpStatus.OK);
    }


    @GetMapping(value = "/{courseId}/reviews")
    public ResponseEntity<Page<ReviewOutput>> getCourseReviews(
        @PathVariable(name = "courseId") String courseId,
        Pageable pageable) {
        return new ResponseEntity<>(
            courseEntityService.getCourseReviews(courseId, pageable),
            HttpStatus.OK);
    }

    @GetMapping(value = "/view/{courseId}")
    public ResponseEntity<CourseOutput> getCourseDetails(
        @PathVariable(name = "courseId") String courseId) {
        return new ResponseEntity<>(
            courseEntityService.getCourseWithLessons(courseId),
            HttpStatus.OK);
    }

    @PostMapping(value = "/{courseId}/enroll")
    public ResponseEntity<Boolean> enrollIntoCourse(
        @PathVariable(name = "courseId") String courseId) {
        return new ResponseEntity<>(
            courseEntityService.enrollUserIntoCourse(courseId),
            HttpStatus.OK);
    }

    @PutMapping (value = "/{courseId}/approve")
    public ResponseEntity<Boolean> approveCourse(
        @PathVariable(name = "courseId") String courseId) {
        return new ResponseEntity<>(
            courseEntityService.approveCourse(courseId),
            HttpStatus.OK);
    }

    @PutMapping(value = "/reject")
    public ResponseEntity<Boolean> rejectCourse(
        @RequestBody RejectCourseInput rejectCourseInput) {
        String courseId = rejectCourseInput.getCourseId();
        String rejectionMessage = rejectCourseInput.getRejectionMessage();
        return new ResponseEntity<>(
            courseEntityService.rejectCourse(courseId, rejectionMessage),
            HttpStatus.OK);
    }

}
