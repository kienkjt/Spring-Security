package com.kjt.springsecurity.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(true, message, data);
    }

    public static <T> APIResponse<T> createFailureResponse(String message) {
        return new APIResponse<>(false, message, null);
    }
    public  static <T> APIResponse<T> createFailureResponse(String message, T data) {
        return new APIResponse<>(false, message, data);
    }
}
