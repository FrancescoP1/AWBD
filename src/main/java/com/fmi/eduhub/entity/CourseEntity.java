package com.fmi.eduhub.entity;

import com.fmi.eduhub.enums.CourseCategoryEnum;
import com.fmi.eduhub.enums.CourseStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "course")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private UUID courseId;

    //@Size(min = 5, message = "Course name must be at least 5 characters long")
    @Column(name = "course_name")
    private String courseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_category")
    private CourseCategoryEnum courseCategory;

    //@Size(min = 20, message = "Description must be at least 20 characters long.")
    @Column(name = "course_description")
    private String courseDescription;

    @Column(name = "thumbnail_key")
    private String thumbnailKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CourseStatusEnum courseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "user_id")
    private UserEntity author;

    @OneToMany(mappedBy = "course", orphanRemoval = true)
    private Set<LessonEntity> lessons = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviews = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCourseEntity> userCourses = new HashSet<>();

    @Column(name = "rejection_message")
    private String rejectionMessage;

    @Column(name = "rating")
    private Double rating = 0.0;



    /*
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<UserCourseEntity> enrolledUsers = new HashSet<>();
     */
}
