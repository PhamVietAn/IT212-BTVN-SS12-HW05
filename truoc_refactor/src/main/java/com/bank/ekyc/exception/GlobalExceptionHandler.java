package com.bank.ekyc.exception;

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
 * Lớp xử lý ngoại lệ tập trung (Global Exception Handler) cho toàn bộ ứng dụng.
 * Sử dụng RestControllerAdvice để tự động bắt các ngoại lệ được ném ra từ các Controller và Service,
 * sau đó đóng gói dữ liệu lỗi trả về cho Client một cách nhất quán và đẹp mắt.
 * 
 * @author Senior Backend Developer
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý ngoại lệ MethodArgumentNotValidException.
     * Ngoại lệ này xảy ra khi các tham số đầu vào được chú thích bằng @Valid vi phạm kiểm tra JSR-303.
     * 
     * @param ex Ngoại lệ validation
     * @return Phản hồi HTTP mã 400 Bad Request kèm chi tiết các trường bị lỗi
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Duyệt qua tất cả các lỗi và gom nhóm dưới dạng: { "tên_trường": "thông_điệp_lỗi" }
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Dữ liệu yêu cầu không hợp lệ. Vui lòng kiểm tra lại thông tin gửi lên.",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý ngoại lệ IllegalArgumentException.
     * Thường là các lỗi kiểm tra nghiệp vụ ở tầng Service (như trùng lặp SĐT hoặc CCCD).
     * 
     * @param ex Ngoại lệ IllegalArgumentException
     * @return Phản hồi HTTP mã 400 Bad Request kèm theo thông điệp lỗi chi tiết
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý tất cả các ngoại lệ chưa được khai báo cụ thể (Catch-All Exception).
     * Trả về lỗi hệ thống chung mã 500 Internal Server Error để bảo mật thông tin nội bộ.
     * 
     * @param ex Ngoại lệ tổng quát
     * @return Phản hồi HTTP mã 500 Internal Server Error kèm thông điệp chung
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        // Trong môi trường Production, nên ghi log (log.error) chi tiết stacktrace tại đây để điều tra lỗi.
        // Tránh trả về stacktrace của Exception cho Client vì lý do bảo mật.
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
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
        private String message;
        private Map<String, String> details;

        public ErrorResponse() {
        }

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> details) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
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
