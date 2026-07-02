package com.bank.ekyc.dto;

/**
 * DTO (Data Transfer Object) chứa thông tin kết quả trả về sau khi đăng ký tài khoản thành công.
 * Phản hồi này sẽ cung cấp ID tài khoản hệ thống, số tài khoản ngân hàng được cấp và trạng thái hiện tại.
 * 
 * @author Senior Backend Developer
 */
public class AccountResponseDTO {

    /**
     * ID duy nhất của tài khoản được sinh ra trong cơ sở dữ liệu.
     */
    private Long accountId;

    /**
     * Số tài khoản ngân hàng chính thức được cấp cho khách hàng.
     */
    private String accountNumber;

    /**
     * Trạng thái hiện tại của tài khoản (ví dụ: PENDING hoặc ACTIVE).
     */
    private String status;

    // --- Constructors ---

    /**
     * Constructor không tham số.
     */
    public AccountResponseDTO() {
    }

    /**
     * Constructor đầy đủ tham số.
     * 
     * @param accountId ID tài khoản trong cơ sở dữ liệu
     * @param accountNumber Số tài khoản ngân hàng được cấp
     * @param status Trạng thái tài khoản (PENDING/ACTIVE)
     */
    public AccountResponseDTO(Long accountId, String accountNumber, String status) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.status = status;
    }

    // --- Getters and Setters ---

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccountResponseDTO{" +
                "accountId=" + accountId +
                ", accountNumber='" + accountNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
