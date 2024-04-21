package com.fmi.eduhub.controller;

import com.fmi.eduhub.dto.input.ReviewInput;
import com.fmi.eduhub.dto.output.ReviewOutput;
import com.fmi.eduhub.service.ReviewEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

  private final ReviewEntityService reviewEntityService;

  @PostMapping(value = "")
  public ResponseEntity<Boolean> createReview(
      @RequestBody @Valid ReviewInput reviewInput) {
    return new ResponseEntity<>(
        reviewEntityService.createReview(reviewInput),
        HttpStatus.CREATED);
  }

  @GetMapping (value = "/{reviewId}")
  public ResponseEntity<ReviewOutput> getReview(
      @PathVariable(name = "reviewId") String reviewId) {
    return new ResponseEntity<>(
        reviewEntityService.getReviewById(reviewId),
        HttpStatus.OK);
  }

  @GetMapping(value = "/pending")
  public ResponseEntity<Page<ReviewOutput>> getPendingReviews(
      Pageable pageable) {
    return new ResponseEntity<>(
        reviewEntityService.getPendingReviews(pageable),
        HttpStatus.OK);
  }

  @PutMapping("/{reviewId}/approve")
  public ResponseEntity<Boolean> approveReview(
      @PathVariable(name = "reviewId") String reviewId) {
    return new ResponseEntity<>(
        reviewEntityService.approveReview(reviewId),
        HttpStatus.OK);
  }

  @DeleteMapping("/{reviewId}/delete")
  public ResponseEntity<Boolean> rejectReview(
      @PathVariable(name = "reviewId") String reviewId) {
    return new ResponseEntity<>(
        reviewEntityService.rejectReview(reviewId),
        HttpStatus.OK);
  }
}
