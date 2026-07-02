package com.bank.ekyc.controller;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;
import com.bank.ekyc.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Lớp kiểm thử đơn vị (Unit Test) cho AccountController sử dụng Spring Boot WebMvcTest.
 * Lớp này chịu trách nhiệm kiểm tra việc tiếp nhận Request, chuyển đổi dữ liệu (Serialization/Deserialization),
 * kích hoạt các ràng buộc kiểm tra JSR-303 (Validation) và xử lý HTTP Status tương ứng.
 * 
 * @author Senior QA & Automation Engineer
 */
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    /**
     * Test case kiểm thử gửi yêu cầu đăng ký mở tài khoản eKYC hợp lệ.
     * Kỳ vọng:
     * - Dữ liệu DTO đầu vào thỏa mãn tất cả ràng buộc JSR-303.
     * - Mock service xử lý thành công và trả về thông tin tài khoản ACTIVE.
     * - Phản hồi HTTP Status 201 Created và body JSON đúng định dạng AccountResponseDTO.
     */
    @Test
    @DisplayName("Đăng ký API thành công - HTTP 201 Created")
    void testRegisterAccount_Success() throws Exception {
        // Arrange
        AccountRequestDTO requestDTO = new AccountRequestDTO(
                "Nguyễn Văn A",
                "0987654321",
                "nguyenvana@gmail.com",
                "012345678901"
        );

        AccountResponseDTO responseDTO = new AccountResponseDTO(
                1L,
                "9987654321",
                "ACTIVE"
        );

        when(accountService.registerAccount(any(AccountRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("9987654321"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Xác minh Service được gọi đúng 1 lần
        verify(accountService, times(1)).registerAccount(any(AccountRequestDTO.class));
    }

    /**
     * Test case kiểm thử gửi yêu cầu đăng ký mở tài khoản để trống toàn bộ thông tin.
     * Kỳ vọng:
     * - Kích hoạt JSR-303 validation và phát hiện lỗi @NotBlank trên tất cả các trường.
     * - Phản hồi HTTP Status 400 Bad Request.
     * - Body JSON chứa chi tiết lỗi (details) tương ứng của từng trường bị trống.
     */
    @Test
    @DisplayName("Đăng ký API thất bại - Các trường bắt buộc bị trống")
    void testRegisterAccount_BlankFields_ReturnsBadRequest() throws Exception {
        // Arrange: Tất cả các trường đều rỗng hoặc null
        AccountRequestDTO requestDTO = new AccountRequestDTO("", "", "", "");

        // Act & Assert
        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Dữ liệu yêu cầu không hợp lệ. Vui lòng kiểm tra lại thông tin gửi lên."))
                .andExpect(jsonPath("$.details.fullName").value("Họ và tên không được để trống"))
                .andExpect(jsonPath("$.details.phone").value("Số điện thoại không được để trống"))
                .andExpect(jsonPath("$.details.email").value("Email không được để trống"))
                .andExpect(jsonPath("$.details.citizenId").value("Số CCCD/CMND không được để trống"));

        // Xác minh Service KHÔNG bao giờ được gọi khi validation thất bại
        verify(accountService, never()).registerAccount(any(AccountRequestDTO.class));
    }

    /**
     * Test case kiểm thử gửi yêu cầu đăng ký có họ tên quá ngắn (1 ký tự).
     * Kỳ vọng:
     * - Vi phạm ràng buộc @Size(min = 2, max = 100) trên trường fullName.
     * - Phản hồi HTTP Status 400 Bad Request.
     * - Chi tiết lỗi của fullName là "Họ và tên phải từ 2 đến 100 ký tự".
     */
    @Test
    @DisplayName("Đăng ký API thất bại - Họ tên quá ngắn")
    void testRegisterAccount_InvalidFullNameLength_ReturnsBadRequest() throws Exception {
        // Arrange: fullName chỉ có 1 ký tự
        AccountRequestDTO requestDTO = new AccountRequestDTO(
                "A",
                "0987654321",
                "nguyenvana@gmail.com",
                "012345678901"
        );

        // Act & Assert
        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.fullName").value("Họ và tên phải từ 2 đến 100 ký tự"));

        verify(accountService, never()).registerAccount(any(AccountRequestDTO.class));
    }

    /**
     * Test case kiểm thử gửi yêu cầu đăng ký có Số điện thoại sai định dạng.
     * Kỳ vọng:
     * - Vi phạm ràng buộc @Pattern định dạng SĐT Việt Nam trên trường phone.
     * - Phản hồi HTTP Status 400 Bad Request.
     * - Chi tiết lỗi của phone chứa thông báo hướng dẫn định dạng đúng.
     */
    @Test
    @DisplayName("Đăng ký API thất bại - Số điện thoại sai định dạng")
    void testRegisterAccount_InvalidPhoneFormat_ReturnsBadRequest() throws Exception {
        // Arrange: Số điện thoại cố định (024...) không phải số di động hợp lệ
        AccountRequestDTO requestDTO = new AccountRequestDTO(
                "Nguyễn Văn A",
                "0241234567",
                "nguyenvana@gmail.com",
                "012345678901"
        );

        // Act & Assert
        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.phone").value("Số điện thoại không đúng định dạng tại Việt Nam (VD: 0912345678 hoặc +84912345678)"));

        verify(accountService, never()).registerAccount(any(AccountRequestDTO.class));
    }

    /**
     * Test case kiểm thử gửi yêu cầu đăng ký có Email sai định dạng.
     * Kỳ vọng:
     * - Vi phạm ràng buộc @Email trên trường email.
     * - Phản hồi HTTP Status 400 Bad Request.
     * - Chi tiết lỗi của email chứa thông báo "Email không đúng định dạng hợp lệ".
     */
    @Test
    @DisplayName("Đăng ký API thất bại - Email sai định dạng")
    void testRegisterAccount_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // Arrange: Email thiếu kí tự @
        AccountRequestDTO requestDTO = new AccountRequestDTO(
                "Nguyễn Văn A",
                "0987654321",
                "nguyenvana.com",
                "012345678901"
        );

        // Act & Assert
        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.email").value("Email không đúng định dạng hợp lệ"));

        verify(accountService, never()).registerAccount(any(AccountRequestDTO.class));
    }

    /**
     * Test case kiểm thử gửi yêu cầu đăng ký có Số CCCD/CMND không đúng 12 chữ số.
     * Kỳ vọng:
     * - Vi phạm ràng buộc @Pattern (12 chữ số) trên trường citizenId.
     * - Phản hồi HTTP Status 400 Bad Request.
     * - Chi tiết lỗi của citizenId là "Số CCCD/CMND phải bao gồm đúng 12 chữ số".
     */
    @Test
    @DisplayName("Đăng ký API thất bại - Số CCCD/CMND không đúng 12 số")
    void testRegisterAccount_InvalidCitizenId_ReturnsBadRequest() throws Exception {
        // Arrange: Số CCCD chỉ có 11 chữ số
        AccountRequestDTO requestDTO = new AccountRequestDTO(
                "Nguyễn Văn A",
                "0987654321",
                "nguyenvana@gmail.com",
                "01234567890"
        );

        // Act & Assert
        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.citizenId").value("Số CCCD/CMND phải bao gồm đúng 12 chữ số"));

        verify(accountService, never()).registerAccount(any(AccountRequestDTO.class));
    }
}
