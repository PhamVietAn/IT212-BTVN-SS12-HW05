package com.bank.ekyc.service.impl;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;
import com.bank.ekyc.entity.Account;
import com.bank.ekyc.repository.AccountRepository;
import com.bank.ekyc.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * Lớp triển khai của AccountService, chịu trách nhiệm xử lý các logic nghiệp vụ
 * liên quan đến quản lý và đăng ký tài khoản eKYC.
 * 
 * @author Senior Backend Developer
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final Random random = new Random();

    /**
     * Constructor injection để tiêm AccountRepository vào lớp.
     * Đây là khuyến nghị thực hành tốt nhất (Best Practice) trong Spring Boot.
     * 
     * @param accountRepository Repository tương tác DB của Account
     */
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Đăng ký mở tài khoản ngân hàng trực tuyến qua eKYC.
     * Nghiệp vụ bao gồm kiểm tra trùng lặp SĐT/CCCD, tự động sinh số tài khoản độc nhất 10 số bắt đầu bằng '99',
     * giả lập quy trình eKYC thành công để chuyển trạng thái ACTIVE và lưu vào DB.
     * 
     * @param requestDTO Dữ liệu yêu cầu đăng ký
     * @return DTO phản hồi kết quả tài khoản vừa tạo
     */
    @Override
    @Transactional
    public AccountResponseDTO registerAccount(AccountRequestDTO requestDTO) {
        // 1. Kiểm tra trùng lặp số CCCD/CMND
        if (accountRepository.existsByCitizenId(requestDTO.getCitizenId())) {
            throw new IllegalArgumentException("Số CCCD/CMND đã tồn tại trên hệ thống");
        }

        // 2. Kiểm tra trùng lặp số điện thoại
        if (accountRepository.existsByPhone(requestDTO.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký tài khoản khác");
        }

        // 3. Tự động sinh số tài khoản duy nhất (10 số, bắt đầu bằng 99)
        String accountNumber = generateUniqueAccountNumber();

        // 4. Khởi tạo đối tượng Entity từ DTO đầu vào
        // Mặc định trạng thái tài khoản là ACTIVE để biểu thị đã đăng ký & xác thực eKYC thành công bước cơ bản.
        Account account = new Account(
                requestDTO.getFullName(),
                requestDTO.getPhone(),
                requestDTO.getEmail(),
                requestDTO.getCitizenId(),
                accountNumber,
                Account.AccountStatus.ACTIVE
        );

        // 5. Lưu vào Database
        Account savedAccount = accountRepository.save(account);

        // 6. Chuyển đổi dữ liệu Entity vừa lưu thành DTO kết quả để trả về cho Client
        return new AccountResponseDTO(
                savedAccount.getId(),
                savedAccount.getAccountNumber(),
                savedAccount.getStatus().name()
        );
    }

    /**
     * Sinh ngẫu nhiên một số tài khoản ngân hàng có độ dài 10 chữ số,
     * bắt đầu bằng tiền tố '99'.
     * Thực hiện kiểm tra lặp trong DB để đảm bảo số tài khoản này chưa từng tồn tại.
     * 
     * @return Chuỗi số tài khoản độc nhất gồm 10 chữ số
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        int maxAttempts = 1000; // Giới hạn số lần thử để tránh lặp vô tận trong trường hợp DB đầy dữ liệu
        int attempts = 0;

        do {
            if (attempts >= maxAttempts) {
                throw new RuntimeException("Hệ thống quá tải: Không thể sinh thêm số tài khoản ngẫu nhiên mới");
            }
            
            // Sinh số ngẫu nhiên 8 chữ số (từ 0 đến 99999999)
            int randomSuffix = random.nextInt(100000000);
            
            // Định dạng chuỗi: "99" + suffix đã đệm đủ 8 chữ số (VD: 9900012345)
            accountNumber = String.format("99%08d", randomSuffix);
            attempts++;
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
