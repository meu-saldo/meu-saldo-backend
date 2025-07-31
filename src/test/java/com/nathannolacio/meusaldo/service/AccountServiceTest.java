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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("Deve retornar uma lista de DTOs de conta quando o usuário possui contas")
    void getAllAccounts_whenUserHasAccounts_shouldReturnAccountDTOList() {
        Long mockUserId = 1L;
        User mockUser = new User();

        mockUser.setId(mockUserId);
        mockUser.setName("testuser");

        Account account1 = new Account(101L, "Conta1", "Descrição", BigDecimal.ZERO, mockUser);
        Account account2 = new Account(102L, "Conta2", "Descrição", BigDecimal.ZERO, mockUser);
        List<Account> accountsFromRepo = List.of(account1, account2);

        when(authUtils.getAuthenticatedUserId()).thenReturn(mockUserId);
        when(accountRepository.findByUserId(mockUserId)).thenReturn(accountsFromRepo);

        List<AccountResponseDTO> result = accountService.getAllAccounts();


        assertThat(result.get(0).id()).isEqualTo(account1.getId());
        assertThat(result.get(0).name()).isEqualTo(account1.getName());

        assertThat(result.get(1).id()).isEqualTo(account2.getId());
        assertThat(result.get(1).name()).isEqualTo(account2.getName());

        verify(authUtils, times(1)).getAuthenticatedUserId();
        verify(accountRepository, times(1)).findByUserId(mockUserId);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o usuário não possui contas")
    void getAllAccounts_whenUserHasNoAccounts_shouldReturnEmptyList() {
        Long mockUserId = 2L;

        when(authUtils.getAuthenticatedUserId()).thenReturn(mockUserId);
        when(accountRepository.findByUserId(mockUserId)).thenReturn(Collections.emptyList());

        List<AccountResponseDTO> result = accountService.getAllAccounts();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(authUtils, times(1)).getAuthenticatedUserId();
        verify(accountRepository, times(1)).findByUserId(mockUserId);
    }

}
