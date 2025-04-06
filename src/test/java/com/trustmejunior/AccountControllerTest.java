package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.CreateAccountRequest;
import com.trustmejunior.request.LoginRequest;
import com.trustmejunior.request.UpdateAccountRequest;
import com.trustmejunior.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account(13, "noname", "123456", "No Name", "noname@gmail.com",
                Date.valueOf("2005-02-02"), AccountRole.MANAGER);
    }

    @Test
    void testLogin() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("noname", "123456");
        when(accountService.login(loginRequest)).thenReturn(testAccount);

        // Act
        Account actualAccount = accountController.login(loginRequest);

        // Assert
        assertNotNull(actualAccount, "Login should return a valid account");
        assertEquals(testAccount.getAccountId(), actualAccount.getAccountId(), "Account ID should match after login");
        assertEquals(testAccount.getUsername(), actualAccount.getUsername(), "Username should match after login");
        verify(accountService).login(loginRequest);
    }

    @Test
    void testGetAccountById() {
        // Arrange
        int accountId = 13;
        when(accountService.getAccountById(accountId)).thenReturn(testAccount);

        // Act
        Account actualAccount = accountController.getAccountById(accountId);

        // Assert
        assertNotNull(actualAccount, "Retrieved account should not be null");
        assertEquals(testAccount.getAccountId(), actualAccount.getAccountId(), "Account ID should match");
        assertEquals(testAccount.getUsername(), actualAccount.getUsername(), "Username should match");
        assertEquals(testAccount.getAccountRole(), actualAccount.getAccountRole(), "Account role should match");
        verify(accountService).getAccountById(accountId);
    }

    @Test
    void testGetAllAccounts() {
        // Arrange
        List<Account> mockAccounts = Arrays.asList(
                testAccount,
                new Account(14, "user2", "pass2", "User Two", "user2@gmail.com",
                        Date.valueOf("2005-02-02"), AccountRole.TENANT));
        when(accountService.getAllAccounts()).thenReturn(mockAccounts);

        // Act
        List<Account> accounts = accountController.getAllAccounts();

        // Assert
        assertNotNull(accounts, "Retrieved accounts list should not be null");
        assertEquals(2, accounts.size(), "Should return correct number of accounts");
        assertEquals("noname", accounts.get(0).getUsername(), "First account username should match");
        verify(accountService).getAllAccounts();
    }

    @Test
    void testUpdatePassword() throws Exception {
        // Arrange
        int id = 5;
        UpdateAccountRequest request = new UpdateAccountRequest("noname", "Rmit@030406",
                "No Name", "noname@gmail.com", Date.valueOf("2005-02-02"), AccountRole.MANAGER);
        Account updatedAccount = new Account(id, request.getUsername(), "Rmit@030406",
                request.getFullName(), request.getEmail(), request.getDob(), request.getAccountRole());

        when(accountService.updateAccount(eq(id), any(UpdateAccountRequest.class))).thenReturn(updatedAccount);

        // Act
        accountController.updateAccount(id, request);

        // Assert
        verify(accountService).updateAccount(id, request);
    }

    @Test
    void testDeleteAccountById() throws Exception {
        // Arrange
        int id = 30;
        doNothing().when(accountService).deleteAccountById(id);

        // Act
        accountController.deleteAccountById(id);

        // Assert
        verify(accountService).deleteAccountById(id);
    }

    @Test
    void testCreateAccount() throws Exception {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest("mouse1", "Mouse@rmit875",
                "Mouse Nguyen", "Mouse@gmail.com", Date.valueOf("2005-02-02"), AccountRole.TENANT);
        Account newAccount = new Account(1, request.getUsername(), request.getPassword(),
                request.getFullName(), request.getEmail(), request.getDob(), request.getAccountRole());

        when(accountService.createAccount(request)).thenReturn(newAccount);

        // Act
        Account createdAccount = accountController.createAccount(request);

        // Assert
        assertNotNull(createdAccount, "Created account should not be null");
        assertEquals(request.getUsername(), createdAccount.getUsername(),
                "Created account username should match request");
        verify(accountService).createAccount(request);
    }

    @Test
    void testUpdateAccount() throws Exception {
        // Arrange
        int accountId = 37;
        UpdateAccountRequest request = new UpdateAccountRequest("glass6969", "Mouse@rmit875",
                "Mouse Nguyen", "Mouse@gmail.com", Date.valueOf("2005-02-02"), AccountRole.TENANT);
        Account updatedAccount = new Account(accountId, request.getUsername(), request.getPassword(),
                request.getFullName(), request.getEmail(), request.getDob(), request.getAccountRole());

        when(accountService.updateAccount(accountId, request)).thenReturn(updatedAccount);

        // Act
        Account result = accountController.updateAccount(accountId, request);

        // Assert
        assertNotNull(result, "Updated account should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Updated account username should match request");
        verify(accountService).updateAccount(accountId, request);
    }
}
