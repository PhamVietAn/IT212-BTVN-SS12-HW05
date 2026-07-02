package com.bank.ekyc.service;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;

/**
 * Interface AccountService định nghĩa các nghiệp vụ liên quan đến quản lý tài khoản ngân hàng.
 * 
 * @author Senior Backend Developer
 */
public interface AccountService {

    /**
     * Thực hiện nghiệp vụ đăng ký mở tài khoản ngân hàng trực tuyến qua eKYC.
     * 
     * @param requestDTO Đối tượng DTO chứa dữ liệu yêu cầu đăng ký của khách hàng (họ tên, email, sđt, CCCD)
     * @return AccountResponseDTO chứa kết quả xử lý (id tài khoản, số tài khoản, trạng thái)
     * @throws IllegalArgumentException Nếu dữ liệu đầu vào vi phạm quy tắc nghiệp vụ như trùng CCCD hoặc trùng SĐT
     */
    AccountResponseDTO registerAccount(AccountRequestDTO requestDTO);
}
