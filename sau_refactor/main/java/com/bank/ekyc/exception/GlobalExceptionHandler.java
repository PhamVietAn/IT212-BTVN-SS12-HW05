package com.bank.ekyc.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp xử lý ngoại lệ tập trung (Global Exception Handler) cho ứng dụng eKYC.
 * Tự động bắt các Exception ném ra từ Controller/Service để định dạng dữ liệu lỗi phản hồi client.
 * 
 * @author Senior Backend Developer
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý các custom exception kế thừa từ BaseException (lỗi nghiệp vụ như trùng lặp, giới hạn hệ thống).
     * Log chi tiết lỗi (ERROR level) kèm theo exception stacktrace để phục vụ truy vết.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.error("Xảy ra ngoại lệ nghiệp vụ eKYC [{}] - HTTP {}: {}", 
                ex.getErrorCode(), ex.getHttpStatus().value(), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getHttpStatus().value(),
                ex.getHttpStatus().getReasonPhrase(),
                ex.getErrorCode(),
                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Xử lý ngoại lệ validation (MethodArgumentNotValidException) khi client gửi request không đúng định dạng.
     * Log cảnh báo (WARN level) để tránh tràn ngập log server bởi các request lỗi của Client.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Dữ liệu client gửi lên vi phạm quy tắc validation: {}", errors);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "ERR_VALIDATION_FAILED",
                "Dữ liệu yêu cầu không hợp lệ. Vui lòng kiểm tra lại thông tin gửi lên.",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý các ngoại lệ IllegalArgumentException thông thường (nếu có).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Xảy ra ngoại lệ IllegalArgumentException: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "ERR_INVALID_ARGUMENT",
                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bộ lọc cuối cùng bắt tất cả các ngoại lệ chưa được xử lý khác (Catch-All Exception).
     * Trả về lỗi chung 500 Internal Server Error và ẩn stacktrace đối với Client để bảo mật.
     * Log chi tiết lỗi ở mức ERROR kèm stacktrace để các kỹ sư hệ thống điều tra nguyên nhân.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Xảy ra lỗi hệ thống nghiêm trọng chưa được kiểm soát: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "ERR_INTERNAL_SERVER_ERROR",
                "Đã xảy ra lỗi hệ thống ngoài ý muốn. Vui lòng liên hệ bộ phận hỗ trợ kỹ thuật.",
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Cấu trúc dữ liệu chuẩn của thông tin lỗi trả về cho Client.
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String errorCode;
        private String message;
        private Map<String, String> details;

        public ErrorResponse() {
        }

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String errorCode, String message, Map<String, String> details) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.errorCode = errorCode;
            this.message = message;
            this.details = details;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getDetails() {
            return details;
        }

        public void setDetails(Map<String, String> details) {
            this.details = details;
        }
    }
}
