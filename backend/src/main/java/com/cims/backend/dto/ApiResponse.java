package com.cims.backend.dto;

public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(0, "success", data);
    }

    public static ApiResponse<Void> successMessage(String message) {
        return new ApiResponse<Void>(0, message, null);
    }

    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<Void>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
