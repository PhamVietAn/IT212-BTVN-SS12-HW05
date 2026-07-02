package com.bank.ekyc.validation;

import com.bank.ekyc.exception.DuplicateResourceException;
import com.bank.ekyc.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lớp Helper Validator chứa logic kiểm tra các ràng buộc nghiệp vụ
 * liên quan đến đăng ký tài khoản eKYC (ví dụ: trùng số CCCD, trùng số điện thoại).
 * 
 * @author Senior Backend Developer
 */
@Component
public class AccountValidator {

    private static final Logger log = LoggerFactory.getLogger(AccountValidator.class);
    private final AccountRepository accountRepository;

    @Autowired
    public AccountValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Kiểm tra trùng lặp số định danh công dân (CCCD/CMND).
     * 
     * @param citizenId Số định danh công dân cần kiểm tra
     * @throws DuplicateResourceException nếu số định danh đã tồn tại trong DB
     */
    public void validateUniqueCitizenId(String citizenId) {
        log.debug("Bắt đầu kiểm tra tính duy nhất cho Citizen ID: {}", citizenId);
        if (accountRepository.existsByCitizenId(citizenId)) {
            log.warn("Citizen ID {} đã tồn tại trên hệ thống", citizenId);
            throw new DuplicateResourceException("Số CCCD/CMND đã tồn tại trên hệ thống", "ERR_DUPLICATE_CITIZEN_ID");
        }
        log.debug("Citizen ID {} hợp lệ (chưa tồn tại)", citizenId);
    }

    /**
     * Kiểm tra trùng lặp số điện thoại đăng ký.
     * 
     * @param phone Số điện thoại cần kiểm tra
     * @throws DuplicateResourceException nếu số điện thoại đã tồn tại trong DB
     */
    public void validateUniquePhone(String phone) {
        log.debug("Bắt đầu kiểm tra tính duy nhất cho SĐT: {}", phone);
        if (accountRepository.existsByPhone(phone)) {
            log.warn("Số điện thoại {} đã được đăng ký tài khoản khác", phone);
            throw new DuplicateResourceException("Số điện thoại đã được đăng ký tài khoản khác", "ERR_DUPLICATE_PHONE");
        }
        log.debug("Số điện thoại {} hợp lệ (chưa tồn tại)", phone);
    }
}
