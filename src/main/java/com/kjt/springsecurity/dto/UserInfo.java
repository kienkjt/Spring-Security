package com.kjt.springsecurity.dto;

import com.kjt.springsecurity.entity.UserAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String department;
    private String position;
}
