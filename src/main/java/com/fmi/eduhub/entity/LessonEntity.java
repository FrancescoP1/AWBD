package com.fmi.eduhub.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class LessonEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID lessonId;

    private String lessonTitle;
    private String lessonDescription;
    private String lessonVideo;
    private String pdfKey;
    private String thumbnailKey;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLessonEntity> userLessons;

    @ManyToOne(targetEntity = CourseEntity.class)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private CourseEntity course;
}
