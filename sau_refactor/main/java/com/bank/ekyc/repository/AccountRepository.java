package com.bank.ekyc.repository;

import com.bank.ekyc.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cung cấp các phương thức truy xuất và thao tác dữ liệu với bảng accounts trong cơ sở dữ liệu.
 * Kế thừa JpaRepository để thừa hưởng các thao tác CRUD cơ bản.
 * 
 * @author Senior Backend Developer
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Tìm kiếm thông tin tài khoản bằng số định danh công dân (citizenId).
     * 
     * @param citizenId Số CCCD/CMND 12 số
     * @return Optional chứa Account nếu tồn tại, ngược lại trả về Optional.empty()
     */
    Optional<Account> findByCitizenId(String citizenId);

    /**
     * Tìm kiếm thông tin tài khoản bằng số tài khoản ngân hàng (accountNumber).
     * 
     * @param accountNumber Số tài khoản ngân hàng
     * @return Optional chứa Account nếu tồn tại, ngược lại trả về Optional.empty()
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Kiểm tra xem số CCCD/CMND đã được sử dụng đăng ký tài khoản nào chưa.
     * 
     * @param citizenId Số CCCD/CMND cần kiểm tra
     * @return true nếu số định danh đã tồn tại, ngược lại false
     */
    boolean existsByCitizenId(String citizenId);

    /**
     * Kiểm tra xem số điện thoại đã được liên kết với tài khoản nào chưa.
     * 
     * @param phone Số điện thoại cần kiểm tra
     * @return true nếu số điện thoại đã tồn tại, ngược lại false
     */
    boolean existsByPhone(String phone);

    /**
     * Kiểm tra xem số tài khoản ngân hàng đã tồn tại trong hệ thống chưa.
     * Phương thức này được sử dụng để tránh xung đột khi sinh số tài khoản ngẫu nhiên.
     * 
     * @param accountNumber Số tài khoản ngân hàng cần kiểm tra
     * @return true nếu số tài khoản đã tồn tại, ngược lại false
     */
    boolean existsByAccountNumber(String accountNumber);
}
