package com.fmi.eduhub.service;

import com.fmi.eduhub.dto.input.LessonInputModel;
import com.fmi.eduhub.dto.output.CourseSimpleOutput;
import com.fmi.eduhub.dto.output.LessonOutput;
import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.LessonEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.enums.UserRoleEnum;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.exception.ResourceNotFoundException;
import com.fmi.eduhub.mapper.CourseEntityMapper;
import com.fmi.eduhub.mapper.LessonEntityMapper;
import com.fmi.eduhub.repository.LessonEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

;

@Service
@RequiredArgsConstructor
public class LessonEntityService {

  private final LessonEntityRepository lessonEntityRepository;
  private final FileUploadService fileUploadService;
  private final UsersEntityService usersEntityService;
  private final UserLessonEntityService userLessonEntityService;

  private final LessonEntityMapper lessonEntityMapper = LessonEntityMapper.INSTANCE;
  private final CourseEntityMapper courseEntityMapper = CourseEntityMapper.INSTANCE;

  private CourseSimpleOutput courseSimpleOutput;


  @Transactional
  public LessonEntity createLessonEntity(LessonInputModel lessonInputModel) {
    LessonEntity lessonEntity =
        lessonEntityMapper.fromLessonInputToEntity(lessonInputModel);
    // convert link to embeddable url
    String lessonVideoUrl = lessonEntity.getLessonVideo();
    lessonEntity.setLessonVideo(getEmbeddableYoutubeUrl(lessonVideoUrl));

    // upload pdf
    if(lessonInputModel.getLessonPdf() != null) {
      // TO DO: validation
      String pdfKey =
          fileUploadService.uploadFile(lessonInputModel.getLessonPdf());
      lessonEntity.setPdfKey(pdfKey);
    }

    if(lessonInputModel.getThumbnailImage() != null) {
      String thumbnailKey =
          fileUploadService.uploadFile(lessonInputModel.getThumbnailImage());
      lessonEntity.setThumbnailKey(thumbnailKey);
    }
    return lessonEntityRepository.save(lessonEntity);
  }

  @Transactional
  public boolean updateLessonEntity(LessonInputModel lessonInputModel) {
    LessonEntity dbLesson =
        getLessonEntityById(UUID.fromString(lessonInputModel.getLessonId()));
    if(lessonInputModel.getLessonTitle() != null) {
      dbLesson.setLessonTitle(lessonInputModel.getLessonTitle());
    }

    if(lessonInputModel.getLessonPdf() != null) {
      //TO DO: validation
      fileUploadService.deleteFile(dbLesson.getPdfKey());
      String newPdfKey =
          fileUploadService.uploadFile(lessonInputModel.getLessonPdf());
      dbLesson.setPdfKey(newPdfKey);
    }

    if(lessonInputModel.getThumbnailImage() != null) {
      // TO DO: validation
      fileUploadService.deleteFile(dbLesson.getThumbnailKey());
      String newThumbnailKey =
          fileUploadService.uploadFile(lessonInputModel.getThumbnailImage());
      dbLesson.setThumbnailKey(newThumbnailKey);
    }

    if(lessonInputModel.getLessonVideo() != null) {
      dbLesson.setLessonVideo(
          getEmbeddableYoutubeUrl(lessonInputModel.getLessonVideo()));
    }
    lessonEntityRepository.save(dbLesson);
    return true;
  }

  public void deleteLesson(LessonEntity lessonEntity) {
    // delete thumbnail and pdf from cloud
    if(lessonEntity.getThumbnailKey() != null) {
      fileUploadService.deleteFile(lessonEntity.getThumbnailKey());
    }
    if(lessonEntity.getPdfKey() != null) {
      fileUploadService.deleteFile(lessonEntity.getPdfKey());
    }
    lessonEntityRepository.delete(lessonEntity);
  }

  public Page<LessonOutput> getLessonsByCourse(CourseEntity course, Pageable pageable) {
    this.courseSimpleOutput = null;
    Page<LessonEntity> lessonEntities =
        lessonEntityRepository.findAllByCourseCourseId(course.getCourseId(), pageable);
    return lessonEntities.map(lessonEntity -> fromLessonEntityToOutput(course, lessonEntity));
  }

  public List<LessonOutput> generateLessonOutputList(List<LessonEntity> lessonEntities) {
    List<LessonOutput> lessonOutputList =
        lessonEntityMapper.fromLessonEntityToOutput(lessonEntities);
    for(int i = 0; i < lessonEntities.size(); ++i) {
      LessonOutput currentOutput = lessonOutputList.get(i);
      LessonEntity currentEntity = lessonEntities.get(i);
      if (currentEntity.getThumbnailKey() != null) {
        currentOutput.setThumbnailUrl(
            fileUploadService.generatePreSignedUrl(currentEntity.getThumbnailKey()));
      }
    }
    return lessonOutputList;
  }

  @Transactional
  public void deleteLessons(List<LessonEntity> lessonEntities) {
    List<String> awsKeys = new ArrayList<>();
    for (LessonEntity currentEntity : lessonEntities) {
      // delete associated files from cloud
      if (currentEntity.getPdfKey() != null) {
        awsKeys.add(currentEntity.getPdfKey());
      }
      if (currentEntity.getThumbnailKey() != null) {
        awsKeys.add(currentEntity.getThumbnailKey());
      }
    }
    if(awsKeys.size() > 0) {
      fileUploadService.deleteFiles(awsKeys);
    }
    lessonEntityRepository.deleteAll(lessonEntities);
  }

  public boolean completeLesson(String lessonId) {
    LessonEntity lesson = getLessonEntityById(UUID.fromString(lessonId));
    UserEntity user = usersEntityService.getCurrentUser();
    return userLessonEntityService.completeLesson(user, lesson);
  }

  public LessonEntity getLessonEntityById(UUID lessonId) {
    Optional<LessonEntity> lessonEntityOptional =
        lessonEntityRepository.findById(lessonId);
    if(lessonEntityOptional.isEmpty()) {
      throw new ResourceNotFoundException(ExceptionConstants.LESSON_NOT_FOUND);
    }
    return lessonEntityOptional.get();
  }

  public LessonOutput fromLessonEntityToOutput(CourseEntity courseEntity, LessonEntity lessonEntity) {
    LessonOutput lessonOutput = lessonEntityMapper.fromLessonEntityToOutput(lessonEntity);
    UserEntity user = usersEntityService.getCurrentUser();
    lessonOutput.setCanView(false);
    if(user != null) {
      lessonOutput.setCanView(true);
      if(canEditLesson(user, lessonEntity, courseEntity)) {
        lessonOutput.setCanEdit(true);
        lessonOutput.setCanView(false);
      }
      if(userLessonEntityService.isUserEnrolledIntoCourse(user.getUserId(), courseEntity.getCourseId())) {
        lessonOutput.setCanComplete(true);
        lessonOutput.setCompleted(false);
      }
      if(userLessonEntityService.isLessonCompleted(user.getUserId(), lessonEntity.getLessonId())) {
        lessonOutput.setCanComplete(false);
        lessonOutput.setCompleted(true);
      }
    }
    lessonOutput.setThumbnailUrl(
        fileUploadService.generatePreSignedUrl(lessonEntity.getThumbnailKey()));
    lessonOutput.setPdfUrl(
        fileUploadService.generatePreSignedDownloadUrl(lessonEntity.getPdfKey()));
    if(this.courseSimpleOutput == null) {
      this.courseSimpleOutput = getCourseSimpleOutput(courseEntity);
    }
    lessonOutput.setCourseOutput(this.courseSimpleOutput);
    return lessonOutput;
  }

  public boolean canEditLesson(UserEntity user, LessonEntity lesson, CourseEntity courseEntity) {
    if(user.getUserId() == courseEntity.getAuthor().getUserId()
        && lesson.getCourse().getCourseId() == courseEntity.getCourseId()) {
      return true;
    }
    return user.getUserType() == UserRoleEnum.ADMIN;
  }

  public CourseSimpleOutput getCourseSimpleOutput(CourseEntity courseEntity) {
    CourseSimpleOutput courseOutput =
        courseEntityMapper.fromCourseEntityToSimpleOutput(courseEntity);
    courseOutput.setThumbnailUrl(
        fileUploadService.generatePreSignedUrl(courseEntity.getThumbnailKey()));
    return courseOutput;
  }

  public String getEmbeddableYoutubeUrl(String url) {
    // Check if the input URL is empty
    if (url == null || url.isEmpty()) {
      return "";
    }
    // Extract the video ID from the URL
    String videoId = extractVideoId(url);
    // If the video ID is empty, return an empty string
    if (videoId.isEmpty()) {
      return "";
    }
    // Create the embeddable YouTube URL
    return "https://www.youtube.com/embed/" + videoId;
  }

  private String extractVideoId(String url) {
    String videoId = "";

    // Pattern to match the YouTube video ID
    String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    Pattern compiledPattern = Pattern.compile(pattern);
    Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract video id.
    if (matcher.find()) {
      videoId = matcher.group();
    }
    return videoId;
  }

}
