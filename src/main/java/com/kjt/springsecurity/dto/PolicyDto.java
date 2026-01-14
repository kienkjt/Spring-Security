package com.kjt.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDto {
    private Long id;
    private String name;
    private String description;
    private String resourceType;
    private String action;
    private Map<String, Object> conditions;
    private String effect;
    private Integer priority;
    private Boolean isActive;
}
