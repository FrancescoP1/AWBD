package com.fmi.eduhub.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

//@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    private OffsetDateTime createdAt;
    private OffsetDateTime lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;
}
