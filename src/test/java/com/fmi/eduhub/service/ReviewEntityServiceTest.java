package com.fmi.eduhub.service;

import com.fmi.eduhub.dto.input.CourseInput;
import com.fmi.eduhub.dto.input.ReviewInput;
import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.ReviewEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.exception.ResourceNotFoundException;
import com.fmi.eduhub.mapper.ReviewEntityMapper;
import com.fmi.eduhub.repository.CourseEntityRepository;
import com.fmi.eduhub.repository.ReviewEntityRepository;
import com.fmi.eduhub.repository.UserCourseEntityRepository;
import com.fmi.eduhub.utils.PageableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewEntityServiceTest {

    @Mock
    private ReviewEntityRepository reviewEntityRepository;
    @Mock
    private CourseEntityRepository courseEntityRepository;
    @Mock
    private UserCourseEntityRepository userCourseEntityRepository;
    @Mock
    private UserCourseEntityService userCourseEntityService;
    @Mock
    private UsersEntityService usersEntityService;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private PageableUtils pageableUtils;

    @InjectMocks
    private ReviewEntityService reviewEntityService;

    private static final UUID  courseId = UUID.randomUUID();
    private static final UUID userId = UUID.randomUUID();
    @BeforeEach
    void setUp() {

    }

    @Test
    void createReview_Success() {
        // Setup
        ReviewInput input = new ReviewInput();
        input.setCourseId(courseId.toString());
        CourseEntity course = new CourseEntity();
        course.setCourseId(courseId);
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        when(courseEntityRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(usersEntityService.getCurrentUser()).thenReturn(user);
        when(reviewEntityService.canReviewCourse(user, course)).thenReturn(true);

        // Act
        boolean result = reviewEntityService.createReview(input);

        // Assert
        assertTrue(result);
        verify(reviewEntityRepository).save(any(ReviewEntity.class));
    }


    @Test
    void createReview_CourseNotFound_ThrowsException() {
        // Setup
        ReviewInput input = new ReviewInput();
        input.setCourseId(courseId.toString());
        when(courseEntityRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reviewEntityService.createReview(input));
    }

    @Test
    void approveReview_Success() {
        // Setup
        String reviewId = UUID.randomUUID().toString();
        ReviewEntity review = new ReviewEntity();
        review.setReviewId(UUID.fromString(reviewId));
        review.setCourse(new CourseEntity());

        when(reviewEntityRepository.findById(UUID.fromString(reviewId))).thenReturn(Optional.of(review));
        when(reviewEntityRepository.findAvgRatingByCourse(any())).thenReturn(4.5);

        // Act
        boolean result = reviewEntityService.approveReview(reviewId);

        // Assert
        assertTrue(result);
        verify(reviewEntityRepository).save(review);
        verify(courseEntityRepository).save(any(CourseEntity.class));
    }

    // Additional tests for rejectReview, getReviewById, getReviewsByCourse, getPendingReviews etc.
}

