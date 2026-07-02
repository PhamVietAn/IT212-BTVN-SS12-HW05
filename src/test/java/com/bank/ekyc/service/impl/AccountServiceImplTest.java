package com.bank.ekyc.service.impl;

import com.bank.ekyc.dto.AccountRequestDTO;
import com.bank.ekyc.dto.AccountResponseDTO;
import com.bank.ekyc.entity.Account;
import com.bank.ekyc.exception.DuplicateResourceException;
import com.bank.ekyc.repository.AccountRepository;
import com.bank.ekyc.service.AccountNumberGenerator;
import com.bank.ekyc.validation.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Lớp kiểm thử đơn vị (Unit Test) cho AccountServiceImpl sử dụng JUnit 5 và Mockito.
 * Được cập nhật để phù hợp với kiến trúc tái cấu trúc mới (SRP/DIP/OCP).
 * 
 * @author Senior QA & Automation Engineer
 */
@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new AccountRequestDTO(
                "Nguyễn Văn A",
                "0987654321",
                "nguyenvana@gmail.com",
                "012345678901"
        );
    }

    /**
     * Test case kiểm thử luồng đăng ký tài khoản thành công (Happy Path).
     */
    @Test
    @DisplayName("Đăng ký tài khoản thành công - Dữ liệu hợp lệ")
    void testRegisterAccount_Success() {
        // Arrange (Thiết lập hành vi cho validator và generator mock)
        doNothing().when(accountValidator).validateUniqueCitizenId(validRequest.getCitizenId());
        doNothing().when(accountValidator).validateUniquePhone(validRequest.getPhone());
        when(accountNumberGenerator.generate()).thenReturn("9912345678");

        Account savedAccount = new Account(
                validRequest.getFullName(),
                validRequest.getPhone(),
                validRequest.getEmail(),
                validRequest.getCitizenId(),
                "9912345678",
                Account.AccountStatus.ACTIVE
        );
        savedAccount.setId(100L);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        AccountResponseDTO response = accountService.registerAccount(validRequest);

        // Assert
        assertNotNull(response, "Response không được phép null");
        assertEquals(100L, response.getAccountId(), "ID tài khoản không khớp");
        assertEquals("9912345678", response.getAccountNumber(), "Số tài khoản không khớp");
        assertEquals("ACTIVE", response.getStatus(), "Trạng thái tài khoản mới phải là ACTIVE");

        // Verify: các dependencies được gọi đúng số lần
        verify(accountValidator, times(1)).validateUniqueCitizenId(validRequest.getCitizenId());
        verify(accountValidator, times(1)).validateUniquePhone(validRequest.getPhone());
        verify(accountNumberGenerator, times(1)).generate();
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    /**
     * Test case kiểm thử đăng ký tài khoản thất bại do trùng lặp số CCCD/CMND.
     */
    @Test
    @DisplayName("Đăng ký thất bại - Trùng số CCCD/CMND")
    void testRegisterAccount_DuplicateCitizenId_ThrowsDuplicateResourceException() {
        // Arrange
        doThrow(new DuplicateResourceException("Số CCCD/CMND đã tồn tại trên hệ thống", "ERR_DUPLICATE_CITIZEN_ID"))
                .when(accountValidator).validateUniqueCitizenId(validRequest.getCitizenId());

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> accountService.registerAccount(validRequest),
                "Kỳ vọng ném ra DuplicateResourceException khi trùng số CCCD"
        );

        assertEquals("Số CCCD/CMND đã tồn tại trên hệ thống", exception.getMessage());
        assertEquals("ERR_DUPLICATE_CITIZEN_ID", exception.getErrorCode());

        // Verify: dừng ngay sau khi validate citizenId thất bại
        verify(accountValidator, times(1)).validateUniqueCitizenId(validRequest.getCitizenId());
        verify(accountValidator, never()).validateUniquePhone(anyString());
        verify(accountNumberGenerator, never()).generate();
        verify(accountRepository, never()).save(any(Account.class));
    }

    /**
     * Test case kiểm thử đăng ký tài khoản thất bại do trùng lặp Số điện thoại.
     */
    @Test
    @DisplayName("Đăng ký thất bại - Trùng số điện thoại")
    void testRegisterAccount_DuplicatePhone_ThrowsDuplicateResourceException() {
        // Arrange
        doNothing().when(accountValidator).validateUniqueCitizenId(validRequest.getCitizenId());
        doThrow(new DuplicateResourceException("Số điện thoại đã được đăng ký tài khoản khác", "ERR_DUPLICATE_PHONE"))
                .when(accountValidator).validateUniquePhone(validRequest.getPhone());

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> accountService.registerAccount(validRequest),
                "Kỳ vọng ném ra DuplicateResourceException khi trùng số điện thoại"
        );

        assertEquals("Số điện thoại đã được đăng ký tài khoản khác", exception.getMessage());
        assertEquals("ERR_DUPLICATE_PHONE", exception.getErrorCode());

        // Verify: gọi validate citizenId thành công, sau đó validate phone thất bại và dừng
        verify(accountValidator, times(1)).validateUniqueCitizenId(validRequest.getCitizenId());
        verify(accountValidator, times(1)).validateUniquePhone(validRequest.getPhone());
        verify(accountNumberGenerator, never()).generate();
        verify(accountRepository, never()).save(any(Account.class));
    }
}
