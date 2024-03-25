package com.fmi.eduhub.exception;

public interface ExceptionConstants {
  String TOKEN_EXPIRED = "Access token is expired";
  String ACCESS_DENIED = "Access denied";
  String INVALID_TOKEN = "Invalid token";
  String UNSUPPORTED_TOKEN = "Unsupported token";
  String USERNAME_NOT_FOUND = "Requested user does not exist";
  String MISSING_HEADERS = "Missing or invalid authorization/refresh headers";
  String REVIEW_NOT_FOUND = "Requested review does not exist!";
  String COURSE_NOT_FOUND = "Requested course does not exist!";
  String LESSON_NOT_FOUND = "Requested lesson does not exist!";
  String LESSON_ALREADY_COMPLETED = "Requested lesson has already been completed!";

  String REVIEW_NOT_ACCESSIBLE = "Only users that have completed the course can review it!";
  String LESSON_NOT_ACCESSIBLE = "You are not allowed to access/edit this lesson!";
  String COURSE_NOT_ACCESSIBLE = "You are not allowed to access/edit this course";
  String PROFILE_NOT_ACCESSIBLE = "You are not allowed to access/edit this user profile!";

  String SOMETHING_WENT_WRONG = "Something went wrong! Please try again later!";


}
