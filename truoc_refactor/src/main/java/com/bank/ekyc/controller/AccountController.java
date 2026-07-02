package com.bank.ekyc.controller;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;
import com.bank.ekyc.service.AccountService;
import jakarta.validation.Valid;
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
        // Gọi Service thực hiện xử lý đăng ký tài khoản
        AccountResponseDTO responseDTO = accountService.registerAccount(requestDTO);
        
        // Trả về kết quả kèm theo HTTP Status 201 Created theo chuẩn RESTful API
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
