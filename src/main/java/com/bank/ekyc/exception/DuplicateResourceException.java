package com.bank.ekyc.exception;

import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ xảy ra khi có sự trùng lặp tài nguyên trong hệ thống (như số điện thoại hoặc CCCD/CMND).
 * Trả về HTTP Status 400 Bad Request.
 * 
 * @author Senior Backend Developer
 */
public class DuplicateResourceException extends BaseException {

    /**
     * Khởi tạo DuplicateResourceException với thông điệp và mã lỗi nghiệp vụ tương ứng.
     * 
     * @param message Thông điệp mô tả chi tiết lỗi
     * @param errorCode Mã lỗi nghiệp vụ (ví dụ: ERR_DUPLICATE_CITIZEN_ID)
     */
    public DuplicateResourceException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
}
