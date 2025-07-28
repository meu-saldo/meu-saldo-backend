package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.AccountRequestDTO;
import com.nathannolacio.meusaldo.dto.AccountResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountAlreadyExistsException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private AccountRequestDTO accountRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        accountRequestDTO = new AccountRequestDTO("Nubank", "Cartão de crédito");
    }

    @Test
    @DisplayName("Deve criar uma conta com sucesso quando o nome for único para o usuário")
    void createAccount_WhenNameIsUnique_ShouldReturnCreatedAccountDTO() {
        when(authUtils.getAuthenticatedUser()).thenReturn(testUser);

        when(accountRepository.existsByNameAndUserId(accountRequestDTO.name(), testUser.getId()))
                .thenReturn(false);

        Account accountToSave = new Account(
                accountRequestDTO.name(),
                accountRequestDTO.description(),
                BigDecimal.ZERO,
                testUser
        );

        Account savedAccount = new Account(accountToSave.getName(), accountToSave.getDescription(), accountToSave.getBalance(), testUser);
        savedAccount.setId(100L);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountResponseDTO result = accountService.createAccount(accountRequestDTO);

        assertNotNull(result);
        assertEquals(savedAccount.getId(), result.id());
        assertEquals(accountRequestDTO.name(), result.name());
        assertEquals(accountRequestDTO.description(), result.description());

        verify(authUtils, times(1)).getAuthenticatedUser();
        verify(accountRepository, times(1)).existsByNameAndUserId(accountRequestDTO.name(), testUser.getId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Deve lançar AccountAlreadyExistsException quando o nome da conta já existir para o usuário")
    void createAccount_WhenNameAlreadyExists_ShouldThrowException() {
        when(authUtils.getAuthenticatedUser()).thenReturn(testUser);

        when(accountRepository.existsByNameAndUserId(accountRequestDTO.name(), testUser.getId()))
                .thenReturn(true);

        assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(accountRequestDTO);
        });

        verify(accountRepository, never()).save(any(Account.class));

        verify(authUtils, times(1)).getAuthenticatedUser();
        verify(accountRepository, times(1)).existsByNameAndUserId(accountRequestDTO.name(), testUser.getId());
    }

}
