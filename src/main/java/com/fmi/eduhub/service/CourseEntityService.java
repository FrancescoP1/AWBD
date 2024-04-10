package com.fmi.eduhub.service;

import com.fmi.eduhub.dto.input.CourseInput;
import com.fmi.eduhub.dto.input.LessonInputModel;
import com.fmi.eduhub.dto.output.CourseItemOutput;
import com.fmi.eduhub.dto.output.CourseOutput;
import com.fmi.eduhub.dto.output.LessonOutput;
import com.fmi.eduhub.dto.output.ReviewOutput;
import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.LessonEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.enums.CourseCategoryEnum;
import com.fmi.eduhub.enums.CourseStatusEnum;
import com.fmi.eduhub.enums.UserRoleEnum;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.exception.ResourceNotAccessibleException;
import com.fmi.eduhub.exception.ResourceNotFoundException;
import com.fmi.eduhub.mapper.CourseEntityMapper;
import com.fmi.eduhub.repository.CourseEntityRepository;
import com.fmi.eduhub.repository.LessonEntityRepository;
import com.fmi.eduhub.utils.PageableUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEntityService {

    private final CourseEntityRepository courseEntityRepository;

    private final UsersEntityService usersEntityService;

    private final LessonEntityService lessonEntityService;

    private final FileUploadService fileUploadService;

    private final LessonEntityRepository lessonEntityRepository;

    private final CourseEntityMapper courseEntityMapper = CourseEntityMapper.INSTANCE;
    private final UserCourseEntityService userCourseEntityService;

    private final ReviewEntityService reviewEntityService;
    private final PageableUtils pageableUtils;

    public ResponseEntity<CourseItemOutput> createCourse(
            CourseInput courseRegisterModel) {
        // set id null, just in case
        courseRegisterModel.setCourseId(null);
        CourseEntity courseEntity =
                courseEntityMapper.fromCourseRegistrationModelToEntity(courseRegisterModel);
        //courseEntity.setCourseId(UUID.randomUUID());
        UserEntity author = usersEntityService.getCurrentUser();
        courseEntity.setAuthor(author);
        if(author.getUserType() == UserRoleEnum.AUTHOR) {
            courseEntity.setCourseStatus(CourseStatusEnum.PENDING);
        } else {
            courseEntity.setCourseStatus(CourseStatusEnum.APPROVED);
        }

        if(courseRegisterModel.getThumbnailImage() != null) {
            // TO DO: validation
            String s3Key = fileUploadService.uploadFile(courseRegisterModel.getThumbnailImage());
            courseEntity.setThumbnailKey(s3Key);
        }
        //System.out.println(courseEntity.getCourseCategory());
        log.info(courseEntity.toString());
        courseEntity = courseEntityRepository.save(courseEntity);
        CourseItemOutput courseItemOutput = courseEntityMapper.fromCourseEntityToItemOutput(courseEntity);
        return new ResponseEntity<>(courseItemOutput, HttpStatus.CREATED);
    }

    @Transactional
    public boolean deleteCourse(String courseId) throws AccessDeniedException {
        Optional<CourseEntity> dbCourse =
            courseEntityRepository.findById(UUID.fromString(courseId));
        if(dbCourse.isPresent()) {
            UserEntity userEntity = usersEntityService.getCurrentUser();
            CourseEntity courseEntity = dbCourse.get();
            if(!canEditCourse(userEntity, courseEntity)) {
                throw new AccessDeniedException(ExceptionConstants.ACCESS_DENIED);
            }
            // delete thumbnail
            fileUploadService.deleteFile(courseEntity.getThumbnailKey());
            // delete lessons
            lessonEntityService.deleteLessons(new ArrayList<>(courseEntity.getLessons()));
            courseEntityRepository.delete(dbCourse.get());
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean approveCourse(String courseId) {
        //System.out.println("APPROVE COURSE STARTED.");
        CourseEntity courseEntity = getCourseById(UUID.fromString(courseId));
        courseEntity.setCourseStatus(CourseStatusEnum.APPROVED);
        courseEntity.setRejectionMessage(null);
        courseEntityRepository.save(courseEntity);
       //System.out.println("courseEntity saved!");
        return true;
    }

    @Transactional
    public boolean rejectCourse(String courseId, String rejectionMessage) {
        CourseEntity courseEntity = getCourseById(UUID.fromString(courseId));
        courseEntity.setCourseStatus(CourseStatusEnum.REJECTED);
        courseEntity.setRejectionMessage(rejectionMessage);
        courseEntityRepository.save(courseEntity);
        return true;
    }

    @Transactional
    public CourseItemOutput updateCourse(CourseInput courseInput) throws AccessDeniedException {
        UserEntity currentUser = usersEntityService.getCurrentUser();
        // TO DO: validation of courseModel

        if(courseInput.getCourseId() != null) {
            Optional<CourseEntity> courseEntityOptional = courseEntityRepository
                .findById(UUID.fromString(courseInput.getCourseId()));
            if(courseEntityOptional.isPresent()) {
                CourseEntity dbEntity = courseEntityOptional.get();
                if(!canEditCourse(currentUser, dbEntity)) {
                    throw new AccessDeniedException(ExceptionConstants.ACCESS_DENIED);
                }

                if(courseInput.getThumbnailImage() != null) {
                    // TO DO: fileValidation
                    fileUploadService.deleteFile(dbEntity.getThumbnailKey());
                    String s3Key = fileUploadService.uploadFile(courseInput.getThumbnailImage());
                    dbEntity.setThumbnailKey(s3Key);
                }

                CourseEntity entityToUpdateFrom =
                    courseEntityMapper.fromInputToEntity(courseInput);
                updateCourseEntity(dbEntity, entityToUpdateFrom);

                if(currentUser.getUserType() == UserRoleEnum.ADMIN) {
                    dbEntity.setCourseStatus(CourseStatusEnum.APPROVED);
                } else {
                    dbEntity.setCourseStatus(CourseStatusEnum.PENDING);
                }

                courseEntityRepository.save(dbEntity);
                return courseEntityMapper.fromCourseEntityToItemOutput(dbEntity);
            }
            throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
        }
        throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
    }

    @Transactional
    public boolean addLessonToCourse(LessonInputModel lessonInputModel) {
        String courseId = lessonInputModel.getCourseId();
        Optional<CourseEntity> dbCourseOptional =
            courseEntityRepository.findById(UUID.fromString(courseId));
        if(dbCourseOptional.isPresent()) {
            CourseEntity courseEntity = dbCourseOptional.get();
            UserEntity userEntity = usersEntityService.getCurrentUser();
            if(!canEditCourse(userEntity, courseEntity)) {
                throw new ResourceNotAccessibleException(ExceptionConstants.COURSE_NOT_ACCESSIBLE);
            }
            lessonInputModel.setCourseId(courseEntity.getCourseId().toString());
            LessonEntity lessonEntity =
                lessonEntityService.createLessonEntity(lessonInputModel);
            // courseEntity.setLastModifiedBy(userId.toString());
            // courseEntity.setLastModifiedAt(OffsetDateTime.now());
            courseEntity.getLessons().add(lessonEntity);
            courseEntityRepository.save(courseEntity);
            return true;
        } else {
            throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
        }
    }

    @Transactional
    public boolean removeLessonFromCourse(String courseId, String lessonId) {
        Optional<CourseEntity> dbCourse =
            courseEntityRepository.findById(UUID.fromString(courseId));
        Optional<LessonEntity> dbLesson =
            lessonEntityRepository.findById(UUID.fromString(lessonId));
        if(dbCourse.isPresent() && dbLesson.isPresent()) {
            LessonEntity lessonEntity = dbLesson.get();
            CourseEntity courseEntity = dbCourse.get();
            courseEntity.getLessons().remove(lessonEntity);
            lessonEntityService.deleteLesson(lessonEntity);
            courseEntityRepository.save(courseEntity);
            return true;
        } else {
            if(dbCourse.isEmpty()) {
                throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
            }
           throw new ResourceNotFoundException(ExceptionConstants.LESSON_NOT_FOUND);
        }
    }

    @Transactional
    public boolean editCourseLesson(LessonInputModel lessonInputModel) {
        String courseId = lessonInputModel.getCourseId();
        String lessonId = lessonInputModel.getLessonId();
        CourseEntity course = getCourseById(UUID.fromString(courseId));
        UserEntity user = usersEntityService.getCurrentUser();
        LessonEntity lesson =
            lessonEntityService.getLessonEntityById(UUID.fromString(lessonId));
        if(!canEditCourse(user, course)) {
            throw new ResourceNotAccessibleException(ExceptionConstants.COURSE_NOT_ACCESSIBLE);
        }
        if(!lessonBelongsToCourse(lesson, course)) {
            throw new ResourceNotAccessibleException(ExceptionConstants.LESSON_NOT_ACCESSIBLE);
        }
        return lessonEntityService.updateLessonEntity(lessonInputModel);
    }

    public Page<CourseItemOutput> getCourses(Pageable pageable) {
        UserEntity user = usersEntityService.getCurrentUser();
        Pageable filteredPageable = pageableUtils.getFilteredPageable(pageable);
        Page<CourseEntity> courseEntityList = new PageImpl<>(new ArrayList<>());
        if(user != null && user.getUserType() == UserRoleEnum.ADMIN) {
            courseEntityList = courseEntityRepository.findAll(filteredPageable);
        }
        if(user != null && user.getUserType() == UserRoleEnum.AUTHOR) {
            courseEntityList =
                courseEntityRepository
                    .findAllByAuthorUserIdOrCourseStatus(
                        user.getUserId(), CourseStatusEnum.APPROVED, filteredPageable);
        }
        if(user == null || user.getUserType() == UserRoleEnum.USER) {
            courseEntityList =
                courseEntityRepository
                    .findAllByCourseStatus(CourseStatusEnum.APPROVED, filteredPageable);
        }

        return courseEntityList.map(this::fromCourseEntityToCourseItemOutput);
    }

    public Page<CourseItemOutput> getPendingCourses(Pageable pageable) {
        UserEntity currentUser = usersEntityService.getCurrentUser();
        if(currentUser == null || currentUser.getUserType() != UserRoleEnum.ADMIN) {
            throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
        }
        pageable = pageableUtils.getFilteredPageable(pageable);
        List<CourseStatusEnum> courseStatuses =
            Arrays.asList(CourseStatusEnum.PENDING, CourseStatusEnum.REJECTED);
        Page<CourseEntity> courseEntityPage =
            courseEntityRepository.findAllByCourseStatusIn(courseStatuses, pageable);
        return  courseEntityPage.map(this::fromCourseEntityToCourseItemOutput);
    }

    public Page<CourseItemOutput> getCoursesEnrolled(Pageable pageable) {
        UserEntity currentUser = usersEntityService.getCurrentUser();
        if(currentUser == null) {
            throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
        }
        pageable = pageableUtils.getFilteredPageable(pageable);
        Page<CourseEntity> courseEntityPage =
            userCourseEntityService.getCoursesEnrolled(currentUser, pageable);
        return courseEntityPage.map(this::fromCourseEntityToCourseItemOutput);
    }

    public Page<CourseItemOutput> getCoursesCompleted(Pageable pageable) {
        UserEntity currentUser = usersEntityService.getCurrentUser();
        if(currentUser == null) {
            throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
        }
        pageable = pageableUtils.getFilteredPageable(pageable);
        Page<CourseEntity> courseEntityPage =
            userCourseEntityService.getCoursesCompleted(currentUser, pageable);
        return courseEntityPage.map(this::fromCourseEntityToCourseItemOutput);
    }

    public Page<CourseItemOutput> getCoursesByAuthor(Pageable pageable) {
        UserEntity user = usersEntityService.getCurrentUser();
        if(user.getUserType() == UserRoleEnum.ADMIN
            || user.getUserType() == UserRoleEnum.AUTHOR) {
            pageable = pageableUtils.getFilteredPageable(pageable);
            Page<CourseEntity> courseEntityPage =
                courseEntityRepository
                    .findAllByAuthorUserId(user.getUserId(), pageable);
            return courseEntityPage.map(this::fromCourseEntityToCourseItemOutput);
        }
        throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
    }

    public CourseItemOutput getCourse(String courseId) {
        UserEntity userEntity = usersEntityService.getCurrentUser();
        if(userEntity == null) {
            throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
        }
        CourseEntity courseEntity = getCourseById(UUID.fromString(courseId));
        if(!canViewCourse(userEntity, courseEntity)) {
            throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
        }
        return fromCourseEntityToCourseItemOutput(courseEntity);
    }

    public Page<CourseItemOutput> getCoursesByCategory(Pageable pageable, String category) {
        CourseCategoryEnum courseCategory;
        try {
            courseCategory = CourseCategoryEnum.valueOf(category);
        } catch (IllegalArgumentException exception) {
            return getCourses(pageable);
        }
        UserEntity user = usersEntityService.getCurrentUser();
        Pageable filteredPageable = pageableUtils.getFilteredPageable(pageable);
        Page<CourseEntity> courseEntityList = new PageImpl<>(new ArrayList<>());
        if(user != null && user.getUserType() == UserRoleEnum.ADMIN) {
            courseEntityList = courseEntityRepository.findAllByCourseCategory(courseCategory, filteredPageable);
        }
        if(user != null && user.getUserType() == UserRoleEnum.AUTHOR) {
            courseEntityList =
                courseEntityRepository
                    .findAllByCourseCategoryAndAuthorUserIdOrCourseCategoryAndCourseStatus(
                        courseCategory, user.getUserId(), courseCategory, CourseStatusEnum.APPROVED, filteredPageable);
        }
        if(user == null || user.getUserType() == UserRoleEnum.USER) {
            courseEntityList =
                courseEntityRepository
                    .findAllByCourseCategoryAndCourseStatus(
                        courseCategory, CourseStatusEnum.APPROVED, filteredPageable);
        }

        return courseEntityList.map(this::fromCourseEntityToCourseItemOutput);
    }

    public Page<LessonOutput> getCourseLessons(String courseId, Pageable pageable) {
        pageable = pageableUtils.getFilteredPageable(pageable);
        CourseEntity course = getCourseById(UUID.fromString(courseId));
        UserEntity user = usersEntityService.getCurrentUser();
        if(canViewCourse(user, course)) {
            return lessonEntityService.getLessonsByCourse(course, pageable);
        } else {
            throw new ResourceNotAccessibleException(ExceptionConstants.COURSE_NOT_ACCESSIBLE);
        }
    }

    public Page<ReviewOutput> getCourseReviews(String courseId, Pageable pageable) {
        pageable = pageableUtils.getFilteredPageable(pageable);
        CourseEntity course = getCourseById(UUID.fromString(courseId));
        UserEntity user = usersEntityService.getCurrentUser();
        if(!canViewCourse(user, course)) {
            throw new ResourceNotAccessibleException(ExceptionConstants.COURSE_NOT_ACCESSIBLE);
        }
        return reviewEntityService.getReviewsByCourse(courseId, pageable);
    }

     public CourseOutput getCourseWithLessons(String courseId) {
        Optional<CourseEntity> dbCourse =
            courseEntityRepository.findById(UUID.fromString(courseId));
        if(dbCourse.isPresent()) {
            CourseEntity dbEntity = dbCourse.get();
            return fromCourseEntityToOutput(dbEntity);
        } else {
            throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
        }
     }

    public void updateCourseEntity(CourseEntity dbEntity, CourseEntity entityToUpdateFrom) {
        dbEntity.setCourseName(entityToUpdateFrom.getCourseName());
        dbEntity.setCourseDescription(entityToUpdateFrom.getCourseDescription());
        dbEntity.setCourseCategory(entityToUpdateFrom.getCourseCategory());
        dbEntity.setLastModifiedAt(OffsetDateTime.now());
        dbEntity.setLastModifiedBy(usersEntityService.getCurrentUser().getUserId().toString());
    }

    public String getThumbnailUrl(CourseEntity courseEntity) {
        if(courseEntity.getThumbnailKey() != null) {
            return fileUploadService
                .generatePreSignedUrl(courseEntity.getThumbnailKey());
        }
        return null;
    }

    private CourseOutput fromCourseEntityToOutput(CourseEntity courseEntity) {
        CourseOutput courseOutput = courseEntityMapper.fromCourseEntityToOutput(courseEntity);
        List<LessonEntity> lessonEntities = new ArrayList<>(courseEntity.getLessons());
        List<LessonOutput> lessonOutputs =
            lessonEntityService.generateLessonOutputList(lessonEntities);
        courseOutput.setLessons(lessonOutputs);
        courseOutput.setThumbnailUrl(
            fileUploadService.generatePreSignedUrl(courseEntity.getThumbnailKey()));
        return courseOutput;
    }

    private CourseItemOutput fromCourseEntityToCourseItemOutput(
        CourseEntity courseEntity) {
        CourseItemOutput itemOutput =
            courseEntityMapper.fromCourseEntityToItemOutput(courseEntity);
        itemOutput.setThumbnailUrl(getThumbnailUrl(courseEntity));
        itemOutput.getAuthor().setProfilePictureUrl(
            usersEntityService.getProfileUrl(courseEntity.getAuthor()));
        UserEntity currentUser = usersEntityService.getCurrentUser();
        itemOutput.setCompleted(false);
        itemOutput.setCanEnroll(false);
        // display for authenticated users
        if(currentUser != null) {
            boolean isUserAlreadyEnrolled = userCourseEntityService
                .isUserEnrolledIntoCourse(currentUser.getUserId(), courseEntity.getCourseId());

            if(!isUserAlreadyEnrolled
                && courseEntity.getCourseStatus() == CourseStatusEnum.APPROVED) {
                itemOutput.setCanEnroll(true);
            } else {
                itemOutput.setCompleted(
                    userCourseEntityService.hasUserCompletedCourse(currentUser, courseEntity));
            }

            // editing and viewing rights for frontend display
            if(currentUser.getUserType() == UserRoleEnum.ADMIN) {
                itemOutput.setCanEditAndView(true);
                itemOutput.setCanApprove(true);
            } else if(currentUser.getUserType() == UserRoleEnum.AUTHOR
                && currentUser.getUserId() == courseEntity.getAuthor().getUserId()) {
                itemOutput.setCanEditAndView(true);
            } else {
                itemOutput.setCanView(canViewCourse(currentUser, courseEntity));
            }
        }
        return itemOutput;
    }

    public boolean enrollUserIntoCourse(String courseId) {
        UserEntity userEntity = usersEntityService.getCurrentUser();
        CourseEntity courseEntity = getCourseById(UUID.fromString(courseId));
        return userCourseEntityService.enrollIntoCourse(userEntity, courseEntity);
    }

    public CourseEntity getCourseById(UUID courseId) {
        Optional<CourseEntity> courseEntityOptional = courseEntityRepository.findById(courseId);
        if(courseEntityOptional.isEmpty()) {
            throw new ResourceNotFoundException(ExceptionConstants.COURSE_NOT_FOUND);
        }
        return courseEntityOptional.get();
    }

    public boolean canEditCourse(UserEntity userEntity, CourseEntity courseEntity) {
        if(userEntity.getUserType() == UserRoleEnum.ADMIN) {
            return true;
        }
        if(userEntity.getUserType() == UserRoleEnum.AUTHOR
            && courseEntity.getAuthor().getUserId().compareTo(userEntity.getUserId()) == 0) {
            return true;
        }
        return false;
    }


    public boolean canViewCourse(UserEntity user, CourseEntity courseEntity) {
        if(user != null) {
            if(courseEntity.getCourseStatus() == CourseStatusEnum.APPROVED) {
                return true;
            }
            if(user.getUserType() == UserRoleEnum.ADMIN) {
                return  true;
            }
            if(user.getUserType() == UserRoleEnum.AUTHOR
                && Objects.equals(courseEntity.getAuthor().getUserId(), user.getUserId())) {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean lessonBelongsToCourse(LessonEntity lesson, CourseEntity course) {
        return lesson.getCourse().equals(course);
    }

}
