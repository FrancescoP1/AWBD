package com.fmi.eduhub.validation;

public interface ValidationMessages {
  String COURSE_NAME_SIZE_VALIDATION_MESSAGE = "Course name must be at least 5 characters long";
  String COURSE_DESCRIPTION_SIZE_VALIDATION_MESSAGE = "Course description must be at least 20 characters long.";
  String COURSE_CATEGORY_NOT_BLANK_VALIDATION_MESSAGE = "Course category must not be blank!";

  String LESSON_TITLE_SIZE_VALIDATION_MESSAGE = "Lesson title must be between 5 and 100 characters long.";
  String LESSON_DESCRIPTION_SIZE_VALIDATION_MESSAGE = "Lesson description must be between 10 and 2000 characters long!";
  String LESSON_COURSE_ID_NOT_BLANK_VALIDATION_MESSAGE = "The lesson's course id must not be blank!";

  String REJECT_COURSE_ID_NOT_BLANK_VALIDATION_MESSAGE = "The rejected course's id must not be blank!";
  String REJECT_COURSE_MESSAGE_SIZE_VALIDATION_MESSAGE = "The message provided for rejecting a course must be between 10 and 1000 characters.";

  String REVIEW_COMMENT_SIZE_VALIDATION_MESSAGE = "The comment of the review must be between 10 and 1000 characters";
  String REVIEW_RATING_RANGE_VALIDATION_MESSAGE = "The rating must be between 1 and 5 stars.";
  String REVIEW_COURSE_ID_NOT_BLANK_VALIDATION_MESSAGE = "The courseId of the course reviewed must not be blank!";

  String USER_PROFILE_ID_NOT_BLANK_MESSAGE = "The user id must not be blank!";

  String FIRST_NAME_SIZE_VALIDATION_MESSAGE = "First name must be between 2 and 100 characters long.";
  String LAST_NAME_SIZE_VALIDATION_MESSAGE = "Last name must be between 2 and 100 characters long.";
  String EMAIL_VALIDATION_MESSAGE = "Please enter a valid email address!";

  String PASSWORD_SIZE_VALIDATION_MESSAGE = "Password must be between 6 and 100 characters long!";
  String PASSWORDS_DO_NOT_MATCH_VALIDATION_MESSAGE = "Password and confirm password must match!";

  String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

}
