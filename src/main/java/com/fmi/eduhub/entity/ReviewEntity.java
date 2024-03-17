package com.fmi.eduhub.entity;

import com.fmi.eduhub.enums.ReviewStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Entity
@Table(name = "review")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID reviewId;

    @NotBlank(message = "Review comment must not be blank")
    private String comment;
    @Range(min = 0, max = 5, message = "Rating must be between 1 and 5")
    private int rating;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private UserEntity reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "course_id")
    private CourseEntity course;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ReviewStatusEnum reviewStatus;
}
