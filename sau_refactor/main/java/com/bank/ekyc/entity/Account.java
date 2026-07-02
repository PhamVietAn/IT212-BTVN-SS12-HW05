package com.bank.ekyc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Thực thể Account đại diện cho thông tin tài khoản ngân hàng trong hệ thống eKYC.
 * Lớp này được ánh xạ trực tiếp tới bảng "accounts" trong cơ sở dữ liệu.
 * 
 * @author Senior Backend Developer
 */
@Entity
@Table(name = "accounts")
public class Account {

    /**
     * ID duy nhất của tài khoản (Khóa chính), tự động tăng trong DB.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Họ và tên đầy đủ của chủ tài khoản.
     */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * Số điện thoại đăng ký tài khoản.
     */
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    /**
     * Địa chỉ Email liên hệ của chủ tài khoản.
     */
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    /**
     * Số định danh công dân (CCCD/CMND) gồm 12 chữ số.
     */
    @Column(name = "citizen_id", nullable = false, unique = true, length = 12)
    private String citizenId;

    /**
     * Số tài khoản ngân hàng được sinh ra ngẫu nhiên hoặc theo logic nghiệp vụ.
     */
    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    /**
     * Trạng thái tài khoản (PENDING: Chờ xác thực eKYC, ACTIVE: Đã kích hoạt/hoạt động).
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    /**
     * Thời gian tạo tài khoản.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật tài khoản gần nhất.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Tự động gán thời gian tạo và cập nhật trước khi lưu vào database.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tự động cập nhật thời gian chỉnh sửa trước khi cập nhật dữ liệu.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Định nghĩa các trạng thái của tài khoản trong hệ thống.
     */
    public enum AccountStatus {
        /**
         * Chờ xác thực eKYC (Ví dụ: chưa hoàn thành đối khớp khuôn mặt hoặc OCR giấy tờ).
         */
        PENDING,
        
        /**
         * Tài khoản đã hoạt động bình thường sau khi eKYC thành công.
         */
        ACTIVE
    }

    // --- Constructors ---

    /**
     * Constructor không tham số (Bắt buộc đối với JPA Entity).
     */
    public Account() {
    }

    /**
     * Constructor đầy đủ tham số (ngoại trừ ID và Timestamps được tự động sinh).
     * 
     * @param fullName Họ và tên chủ tài khoản
     * @param phone Số điện thoại
     * @param email Địa chỉ email
     * @param citizenId Số CCCD
     * @param accountNumber Số tài khoản ngân hàng
     * @param status Trạng thái tài khoản
     */
    public Account(String fullName, String phone, String email, String citizenId, String accountNumber, AccountStatus status) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.citizenId = citizenId;
        this.accountNumber = accountNumber;
        this.status = status;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", citizenId='" + citizenId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
