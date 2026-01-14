package com.kjt.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private Long id;
    private String title;
    private String content;
    private Long ownerId;
    private String ownerUsername;
    private String department;
    private Integer classificationLevel;
    private String status;
}
