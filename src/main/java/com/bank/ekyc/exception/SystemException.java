package com.bank.ekyc.exception;

import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ xảy ra khi có lỗi logic hệ thống nội bộ (như không thể tạo số tài khoản ngẫu nhiên mới).
 * Trả về HTTP Status 500 Internal Server Error.
 * 
 * @author Senior Backend Developer
 */
public class SystemException extends BaseException {

    /**
     * Khởi tạo SystemException với thông điệp và mã lỗi nghiệp vụ tương ứng.
     * 
     * @param message Thông điệp mô tả chi tiết lỗi
     * @param errorCode Mã lỗi nghiệp vụ hệ thống (ví dụ: ERR_SYSTEM_LIMIT_EXCEEDED)
     */
    public SystemException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
