package com.bank.ekyc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) chứa thông tin yêu cầu đăng ký mở tài khoản cơ bản.
 * Lớp này sử dụng các annotation JSR-303 để kiểm tra tính hợp lệ của dữ liệu đầu vào (Validation).
 * 
 * @author Senior Backend Developer
 */
public class AccountRequestDTO {

    /**
     * Họ và tên đầy đủ của khách hàng đăng ký mở tài khoản.
     * Bắt buộc phải nhập và có độ dài từ 2 đến 100 ký tự.
     */
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String fullName;

    /**
     * Số điện thoại đăng ký tài khoản.
     * Bắt buộc phải nhập và tuân thủ định dạng số điện thoại Việt Nam.
     */
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không đúng định dạng tại Việt Nam (VD: 0912345678 hoặc +84912345678)")
    private String phone;

    /**
     * Địa chỉ email liên hệ.
     * Bắt buộc phải nhập và phải đúng định dạng email tiêu chuẩn.
     */
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng hợp lệ")
    private String email;

    /**
     * Số CCCD/CMND (Citizen Identity Card Number) gồm đúng 12 chữ số.
     * Bắt buộc phải nhập và phải đúng 12 chữ số từ 0-9.
     */
    @NotBlank(message = "Số CCCD/CMND không được để trống")
    @Pattern(regexp = "^[0-9]{12}$", message = "Số CCCD/CMND phải bao gồm đúng 12 chữ số")
    private String citizenId;

    // --- Constructors ---

    /**
     * Constructor không tham số.
     */
    public AccountRequestDTO() {
    }

    /**
     * Constructor đầy đủ tham số.
     * 
     * @param fullName Họ và tên
     * @param phone Số điện thoại
     * @param email Địa chỉ email
     * @param citizenId Số định danh công dân (12 số)
     */
    public AccountRequestDTO(String fullName, String phone, String email, String citizenId) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.citizenId = citizenId;
    }

    // --- Getters and Setters ---

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

    @Override
    public String toString() {
        return "AccountRequestDTO{" +
                "fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", citizenId='" + citizenId + '\'' +
                '}';
    }
}
