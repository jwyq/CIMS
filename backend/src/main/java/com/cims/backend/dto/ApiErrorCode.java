package com.cims.backend.dto;

public final class ApiErrorCode {

    private ApiErrorCode() {
    }

    public static final int UNAUTHORIZED = 40101;
    public static final int FORBIDDEN = 40301;
    public static final int BAD_REQUEST = 40001;
    public static final int INTERNAL_ERROR = 50001;
}
