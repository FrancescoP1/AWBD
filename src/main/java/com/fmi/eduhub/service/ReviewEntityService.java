package com.fmi.eduhub.service;

import com.fmi.eduhub.dto.input.ReviewInput;
import com.fmi.eduhub.dto.output.ReviewOutput;
import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.ReviewEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.enums.ReviewStatusEnum;
import com.fmi.eduhub.enums.UserRoleEnum;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.exception.ResourceNotAccessibleException;
import com.fmi.eduhub.exception.ResourceNotFoundException;
import com.fmi.eduhub.mapper.ReviewEntityMapper;
import com.fmi.eduhub.repository.CourseEntityRepository;
import com.fmi.eduhub.repository.ReviewEntityRepository;
import com.fmi.eduhub.repository.UserCourseEntityRepository;
import com.fmi.eduhub.utils.PageableUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewEntityService {

  private final ReviewEntityMapper reviewEntityMapper =
      ReviewEntityMapper.INSTANCE;
  private final ReviewEntityRepository reviewEntityRepository;
  private final UsersEntityService usersEntityService;
  private final CourseEntityRepository courseEntityRepository;
  private final UserCourseEntityRepository userCourseEntityRepository;
  private final UserCourseEntityService userCourseEntityService;
  private final FileUploadService fileUploadService;
  private final PageableUtils pageableUtils;

  @Transactional
  public boolean createReview(ReviewInput reviewInput) {
    reviewInput.setReviewId(null);
    ReviewEntity reviewEntity = reviewEntityMapper.fromInputToEntity(reviewInput);
    Optional<CourseEntity> courseReviewed =
        courseEntityRepository.findById(reviewEntity.getCourse().getCourseId());
    if(courseReviewed.isEmpty()) {
      throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
    }
    UserEntity userEntity = usersEntityService.getCurrentUser();
    CourseEntity courseEntity = courseReviewed.get();
    if(!canReviewCourse(userEntity, courseEntity)) {
      throw new ResourceNotAccessibleException(ExceptionConstants.REVIEW_NOT_ACCESSIBLE);
    }
    reviewEntity.setReviewer(userEntity);
    reviewEntity.setCourse(courseEntity);
    reviewEntity.setReviewStatus(ReviewStatusEnum.PENDING);
    reviewEntityRepository.save(reviewEntity);
    return true;
  }

  @Transactional
  public boolean approveReview(String reviewId) {
    ReviewEntity reviewEntity =
        findReviewEntityById(UUID.fromString(reviewId));
    reviewEntity.setReviewStatus(ReviewStatusEnum.APPROVED);
    reviewEntityRepository.save(reviewEntity);
    UUID courseId = reviewEntity.getCourse().getCourseId();
    System.out.println("before average");
    double courseRating =
        reviewEntityRepository
            .findAvgRatingByCourse(courseId);
    CourseEntity courseEntity = reviewEntity.getCourse();
    courseEntity.setRating(courseRating);
    courseEntityRepository.save(courseEntity);
    return true;
  }

  @Transactional
  public boolean rejectReview(String reviewId) {
    ReviewEntity reviewEntity = findReviewEntityById(UUID.fromString(reviewId));
    reviewEntityRepository.delete(reviewEntity);
    return true;
  }

  public ReviewOutput getReviewById(String reviewId) {
    Optional<ReviewEntity> dbReviewOptional =
        reviewEntityRepository.findById(UUID.fromString(reviewId));
    if(dbReviewOptional.isPresent()){
      ReviewEntity reviewEntity = dbReviewOptional.get();
      ReviewOutput reviewOutput = reviewEntityMapper.fromEntityToOutput(reviewEntity);
      reviewOutput.getReviewer()
          .setProfilePictureUrl(
              usersEntityService
                  .getProfileUrl(reviewEntity.getReviewer()));
      reviewOutput.getCourse().setThumbnailUrl(
          fileUploadService.generatePreSignedUrl(reviewEntity.getCourse().getThumbnailKey()));
      return reviewOutput;
    }
    throw new ResourceNotFoundException(ExceptionConstants.REVIEW_NOT_FOUND);
  }

  public Page<ReviewOutput> getReviewsByCourse(String courseId, Pageable pageable) {
    UserEntity user = usersEntityService.getCurrentUser();
    Page<ReviewEntity> reviewEntityPage;
    if(user.getUserType() == UserRoleEnum.ADMIN) {
      reviewEntityPage = reviewEntityRepository.findAllByCourseCourseId(UUID.fromString(courseId), pageable);
    } else {
      reviewEntityPage =
          reviewEntityRepository
              .findAllByCourseCourseIdAndReviewStatus(UUID.fromString(courseId), ReviewStatusEnum.APPROVED, pageable);
    }
    return reviewEntityPage.map(this::fromReviewEntityToOutput);
  }

  public Page<ReviewOutput> getPendingReviews(Pageable pageable) {
    UserEntity currentUser = usersEntityService.getCurrentUser();
    if(currentUser == null || currentUser.getUserType() != UserRoleEnum.ADMIN) {
      throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
    }
    pageable = pageableUtils.getFilteredPageable(pageable);
    Page<ReviewEntity> reviewEntityPage = reviewEntityRepository.findAllByReviewStatus(ReviewStatusEnum.PENDING, pageable);
    return reviewEntityPage.map(this::fromReviewEntityToOutput);
  }

  public ReviewEntity findReviewEntityById(UUID id) {
    Optional<ReviewEntity> optionalReview = reviewEntityRepository.findById(id);
    if(optionalReview.isEmpty()) {
      throw new ResourceNotAccessibleException(ExceptionConstants.REVIEW_NOT_FOUND);
    }
    return  optionalReview.get();
  }

  public ReviewOutput fromReviewEntityToOutput(ReviewEntity reviewEntity) {
    ReviewOutput reviewOutput = reviewEntityMapper.fromEntityToOutput(reviewEntity);
    UserEntity user = usersEntityService.getCurrentUser();
    reviewOutput.setCanApprove(false);
    reviewOutput.setCanDelete(false);
    reviewOutput.setApproved(false);
    reviewOutput.setCanView(true);
    if(user.getUserId() == reviewEntity.getReviewer().getUserId()) {
      reviewOutput.setCanDelete(true);
    }
    if(user.getUserType() == UserRoleEnum.ADMIN) {
      reviewOutput.setCanDelete(true);
      reviewOutput.setCanApprove(true);
      reviewOutput.setCanView(false);
    }
    if(reviewEntity.getReviewStatus() == ReviewStatusEnum.APPROVED) {
      reviewOutput.setApproved(true);
      reviewOutput.setCanApprove(false);
    }
    reviewOutput.getReviewer().setProfilePictureUrl(
        fileUploadService.generatePreSignedUrl(reviewEntity.getReviewer().getProfilePictureKey()));
    reviewOutput.getCourse().setThumbnailUrl(
        fileUploadService.generatePreSignedUrl(reviewEntity.getCourse().getThumbnailKey()));

    return reviewOutput;
  }

  public boolean canReviewCourse(UserEntity userEntity, CourseEntity courseEntity) {
    return
        userCourseEntityService.hasUserCompletedCourse(userEntity, courseEntity)
            && !reviewEntityRepository.existsByReviewerAndCourse(userEntity, courseEntity);
  }
}
