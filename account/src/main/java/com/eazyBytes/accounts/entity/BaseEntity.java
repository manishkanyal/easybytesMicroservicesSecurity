package com.eazyBytes.accounts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//This is for Annonotations used in dto package in BaseEntity @LastModifiedBy @CreatedBy auditAwareImpl is a bean
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    //Here updatable = false means Whenever a record will be updated in a table i don't want this column in table to be updated .
    //Values in this column can only be inserted once and cannot be updated . It is like a final keyword for variables in java
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable=false)
    private String createdBy;

    //Here insertable = false means Whenever a record will be inserted in a table i don't want this column in table to be have value .
    //Values in this column can only be inserted when there is update query not insert query. In this initially when we are entering
    // values in table, this field will be null but when we will be updating this column then only it will contain value.
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(insertable = false)
    private String updatedBy;

}
