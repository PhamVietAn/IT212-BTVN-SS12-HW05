package com.bank.ekyc.service;

/**
 * Interface AccountNumberGenerator định nghĩa hợp đồng sinh số tài khoản ngân hàng.
 * Đảm bảo tính mở rộng (OCP) và đảo ngược phụ thuộc (DIP).
 * 
 * @author Senior Backend Developer
 */
public interface AccountNumberGenerator {

    /**
     * Sinh số tài khoản ngân hàng duy nhất hợp lệ.
     * 
     * @return Chuỗi số tài khoản duy nhất
     */
    String generate();
}
