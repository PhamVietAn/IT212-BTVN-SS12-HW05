package com.bank.ekyc.service.impl;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;
import com.bank.ekyc.entity.Account;
import com.bank.ekyc.repository.AccountRepository;
import com.bank.ekyc.service.AccountNumberGenerator;
import com.bank.ekyc.service.AccountService;
import com.bank.ekyc.validation.AccountValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp triển khai của AccountService. Xử lý nghiệp vụ đăng ký mở tài khoản eKYC.
 * Các logic validate và sinh số tài khoản đã được tách rời sang các thành phần phụ trợ (SRP/OCP/DIP).
 * 
 * @author Senior Backend Developer
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AccountValidator accountValidator;
    private final AccountNumberGenerator accountNumberGenerator;

    /**
     * Constructor injection tiêm các phụ thuộc tương ứng (DIP).
     */
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountValidator accountValidator,
                              AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountValidator = accountValidator;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    /**
     * Thực hiện đăng ký mở tài khoản eKYC.
     * <p>
     * THIẾT KẾ GIAO DỊCH (TRANSACTION DESIGN):
     * - Bỏ annotation {@code @Transactional} trên toàn bộ phương thức này.
     * - Lý do: Logic validate và logic sinh số tài khoản (có chứa vòng lặp truy vấn DB) không chạy trong transaction ghi.
     * - Việc này giúp giải phóng Connection Pool nhanh chóng, giảm thiểu tối đa giữ khóa và tránh Transaction Timeout.
     * - Khi gọi {@code accountRepository.save(account)}, Spring Data JPA sẽ tự động tạo và cam kết một transaction ngắn
     *   chỉ riêng cho hoạt động ghi dữ liệu vào database.
     * </p>
     * 
     * @param requestDTO Đối tượng chứa dữ liệu yêu cầu đăng ký
     * @return DTO kết quả tài khoản vừa được tạo thành công
     */
    @Override
    public AccountResponseDTO registerAccount(AccountRequestDTO requestDTO) {
        log.info("Bắt đầu xử lý đăng ký tài khoản eKYC. Tên chủ thẻ: {}", maskName(requestDTO.getFullName()));

        // 1. Kiểm tra trùng lặp SĐT/CCCD sử dụng lớp Validator chuyên trách (SRP)
        accountValidator.validateUniqueCitizenId(requestDTO.getCitizenId());
        accountValidator.validateUniquePhone(requestDTO.getPhone());

        // 2. Sinh số tài khoản ngân hàng duy nhất thông qua interface Abstraction (DIP/OCP)
        String accountNumber = accountNumberGenerator.generate();

        // 3. Khởi tạo đối tượng Entity từ DTO đầu vào
        Account account = new Account(
                requestDTO.getFullName(),
                requestDTO.getPhone(),
                requestDTO.getEmail(),
                requestDTO.getCitizenId(),
                accountNumber,
                Account.AccountStatus.ACTIVE
        );

        // 4. Lưu thông tin vào Database (sử dụng transaction ngầm của Spring Data JPA)
        log.debug("Đang thực hiện lưu thông tin tài khoản eKYC vào database...");
        Account savedAccount = accountRepository.save(account);
        log.info("Lưu database thành công. ID tài khoản: {}, Số tài khoản: {}", savedAccount.getId(), savedAccount.getAccountNumber());

        // 5. Chuyển đổi dữ liệu Entity sang DTO kết quả
        return new AccountResponseDTO(
                savedAccount.getId(),
                savedAccount.getAccountNumber(),
                savedAccount.getStatus().name()
        );
    }

    /**
     * Tiện ích che tên chủ tài khoản để bảo mật thông tin log.
     */
    private String maskName(String name) {
        if (name == null || name.length() <= 2) {
            return "***";
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1);
    }
}
