package com.bank.ekyc.service.impl;

import com.bank.ekyc.exception.SystemException;
import com.bank.ekyc.repository.AccountRepository;
import com.bank.ekyc.service.AccountNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Lớp triển khai của AccountNumberGenerator.
 * Thực hiện sinh ngẫu nhiên số tài khoản ngân hàng gồm 10 chữ số bắt đầu bằng "99".
 * Sử dụng ThreadLocalRandom để tối ưu hóa hiệu năng trong môi trường đa luồng (multi-threaded).
 * 
 * @author Senior Backend Developer
 */
@Component
public class RandomAccountNumberGenerator implements AccountNumberGenerator {

    private static final Logger log = LoggerFactory.getLogger(RandomAccountNumberGenerator.class);
    
    private final AccountRepository accountRepository;
    
    // Cấu hình cứng được tách ra làm các hằng số (Constants)
    private static final String PREFIX = "99";
    private static final int MAX_ATTEMPTS = 1000;

    @Autowired
    public RandomAccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Sinh số tài khoản ngân hàng duy nhất 10 số, bắt đầu bằng "99".
     * Kiểm tra lặp trong DB để đảm bảo tính duy nhất.
     * 
     * @return Chuỗi số tài khoản độc nhất
     * @throws SystemException Nếu vượt quá số lần thử cấu hình cho phép
     */
    @Override
    public String generate() {
        String accountNumber;
        int attempts = 0;

        do {
            if (attempts >= MAX_ATTEMPTS) {
                log.error("Hệ thống quá tải: Không thể sinh số tài khoản duy nhất sau {} lần thử", MAX_ATTEMPTS);
                throw new SystemException(
                    "Hệ thống quá tải: Không thể sinh thêm số tài khoản ngẫu nhiên mới", 
                    "ERR_SYSTEM_LIMIT_EXCEEDED"
                );
            }
            
            // Sinh số ngẫu nhiên 8 chữ số sử dụng ThreadLocalRandom để tránh thread contention
            int randomSuffix = ThreadLocalRandom.current().nextInt(100000000);
            accountNumber = String.format("%s%08d", PREFIX, randomSuffix);
            attempts++;
            
            log.trace("Thử sinh số tài khoản: {} (Lần thử: {})", accountNumber, attempts);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        log.debug("Đã sinh số tài khoản duy nhất thành công sau {} lần thử: {}", attempts, accountNumber);
        return accountNumber;
    }
}
