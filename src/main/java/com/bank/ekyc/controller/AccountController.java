package com.bank.ekyc.controller;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;
import com.bank.ekyc.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cung cấp các API liên quan đến quản lý tài khoản ngân hàng eKYC.
 * Lớp này xử lý các yêu cầu HTTP tiếp nhận từ Client.
 * 
 * @author Senior Backend Developer
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    
    private final AccountService accountService;

    /**
     * Constructor injection để tiêm AccountService vào Controller.
     * 
     * @param accountService Service xử lý nghiệp vụ tài khoản
     */
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * API Endpoint: Đăng ký mở tài khoản ngân hàng trực tuyến qua eKYC.
     * Tiếp nhận yêu cầu HTTP POST tại đường dẫn /api/accounts/register.
     * 
     * @param requestDTO Đối tượng chứa dữ liệu đăng ký gửi lên từ Client.
     *                   Sử dụng @Valid để kích hoạt kiểm tra tính hợp lệ của dữ liệu đầu vào (JSR-303).
     * @return ResponseEntity chứa AccountResponseDTO (kết quả sau đăng ký) và HTTP Status 201 Created.
     */
    @PostMapping("/register")
    public ResponseEntity<AccountResponseDTO> registerAccount(@Valid @RequestBody AccountRequestDTO requestDTO) {
        String maskedPhone = maskPhone(requestDTO.getPhone());
        String maskedEmail = maskEmail(requestDTO.getEmail());
        
        log.info("Nhận HTTP POST request tại /api/accounts/register. Phone: {}, Email: {}", maskedPhone, maskedEmail);
        
        // Gọi Service thực hiện xử lý đăng ký tài khoản
        AccountResponseDTO responseDTO = accountService.registerAccount(requestDTO);
        
        log.info("Trả về kết quả thành công cho request /api/accounts/register. Số tài khoản được cấp: {}", responseDTO.getAccountNumber());
        
        // Trả về kết quả kèm theo HTTP Status 201 Created theo chuẩn RESTful API
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Tiện ích che bớt số điện thoại nhạy cảm của khách hàng để bảo mật log.
     * Ví dụ: 0912345678 -> 091****678
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "****";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }

    /**
     * Tiện ích che bớt email nhạy cảm của khách hàng để bảo mật log.
     * Ví dụ: user123@gmail.com -> us*****@gmail.com
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }
        int atIndex = email.indexOf("@");
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (username.length() <= 2) {
            return "**" + domain;
        }
        return username.substring(0, 2) + "*****" + domain;
    }
}
