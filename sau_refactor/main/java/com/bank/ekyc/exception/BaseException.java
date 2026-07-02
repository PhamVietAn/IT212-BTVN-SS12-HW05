package com.bank.ekyc.exception;

import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ cơ sở cho toàn bộ ứng dụng eKYC.
 * Lưu trữ mã lỗi nghiệp vụ (errorCode) và mã trạng thái HTTP (httpStatus).
 * 
 * @author Senior Backend Developer
 */
public class BaseException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    /**
     * Khởi tạo một BaseException mới.
     * 
     * @param message Thông điệp lỗi chi tiết
     * @param errorCode Mã lỗi nghiệp vụ riêng biệt của hệ thống
     * @param httpStatus Mã trạng thái HTTP trả về cho client
     */
    public BaseException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
