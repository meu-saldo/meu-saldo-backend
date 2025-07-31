package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.AccountRequestDTO;
import com.nathannolacio.meusaldo.dto.AccountResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long ACCOUNT_ID = 100L;
    private static final String ACCOUNT_NAME = "Bradesco";
    private static final String ACCOUNT_DESCRIPTION = "Cartão de crédito";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;
    private AccountRequestDTO accountRequestDTO;

    @BeforeEach
    void globalSetup() {

        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setName("testuser");

        testAccount = new Account(ACCOUNT_NAME, ACCOUNT_DESCRIPTION, BigDecimal.ZERO, testUser);
        testAccount.setId(ACCOUNT_ID);

        accountRequestDTO = new AccountRequestDTO(ACCOUNT_NAME, ACCOUNT_DESCRIPTION);
    }

    @Nested
    @DisplayName("Quando criando uma conta")
    class WhenCreatingAccount {

        @BeforeEach
        void setUp() {
            // Mocking específico para os testes de criação de conta
            when(authUtils.getAuthenticatedUser()).thenReturn(testUser);
        }

        @Test
        @DisplayName("Deve criar com sucesso se o nome for único")
        void createAccount_WhenNameIsUnique_ShouldSucceed() {
            // Arrange
            when(accountRepository.existsByNameAndUserId(ACCOUNT_NAME, USER_ID)).thenReturn(false);
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

            // Act
            AccountResponseDTO result = accountService.createAccount(accountRequestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(ACCOUNT_ID, result.id());
            assertEquals(ACCOUNT_NAME, result.name());

            verify(accountRepository).existsByNameAndUserId(ACCOUNT_NAME, USER_ID);
            verify(accountRepository).save(any(Account.class));
        }

        @Test
        @DisplayName("Deve lançar exceção se o nome já existir")
        void createAccount_WhenNameAlreadyExists_ShouldThrowException() {
            // Arrange
            when(accountRepository.existsByNameAndUserId(ACCOUNT_NAME, USER_ID)).thenReturn(true);

            // Act & Assert
            assertThrows(AccountAlreadyExistsException.class, () -> accountService.createAccount(accountRequestDTO));

            verify(accountRepository, never()).save(any(Account.class));
        }
    }

    @Nested
    @DisplayName("Quando buscando todas as contas")
    class WhenGettingAllAccounts {

        @BeforeEach
        void setUp() {
            // Mocking específico para os testes de busca
            when(authUtils.getAuthenticatedUserId()).thenReturn(USER_ID);
        }

        @Test
        @DisplayName("Deve retornar uma lista de contas se o usuário possuir")
        void getAllAccounts_WhenUserHasAccounts_ShouldReturnAccountList() {
            // Arrange
            Account anotherAccount = new Account("Conta2", "Desc", BigDecimal.TEN, testUser);
            anotherAccount.setId(102L);
            List<Account> userAccounts = List.of(testAccount, anotherAccount);
            when(accountRepository.findByUserId(USER_ID)).thenReturn(userAccounts);

            // Act
            List<AccountResponseDTO> result = accountService.getAllAccounts();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(testAccount.getId());
            assertThat(result.get(1).id()).isEqualTo(anotherAccount.getId());

            verify(accountRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia se o usuário não possuir contas")
        void getAllAccounts_WhenUserHasNoAccounts_ShouldReturnEmptyList() {
            // Arrange
            when(accountRepository.findByUserId(USER_ID)).thenReturn(Collections.emptyList());

            // Act
            List<AccountResponseDTO> result = accountService.getAllAccounts();

            // Assert
            assertThat(result).isNotNull().isEmpty();
            verify(accountRepository).findByUserId(USER_ID);
        }
    }

    @Nested
    @DisplayName("Quando desativando uma conta")
    class WhenDeactivatingAccount {

        // CORREÇÃO: O método original deactiveAccount usa getAuthenticatedUserId, então o mock deve ser configurado.
        @BeforeEach
        void setUp() {
            when(authUtils.getAuthenticatedUserId()).thenReturn(USER_ID);
        }

        @Test
        @DisplayName("Deve desativar com sucesso se a conta existir")
        void deactiveAccount_WhenAccountExists_ShouldSucceed() {
            // Arrange
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

            // Act
            accountService.deactiveAccount(ACCOUNT_ID);

            // Assert
            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
            verify(accountRepository).save(accountCaptor.capture());

            Account savedAccount = accountCaptor.getValue();
            assertThat(savedAccount.isActive()).isFalse();
            assertThat(savedAccount.getId()).isEqualTo(ACCOUNT_ID);
        }

        @Test
        @DisplayName("Deve lançar exceção se a conta não existir")
        void deactiveAccount_WhenAccountDoesNotExist_ShouldThrowException() {
            // Arrange
            Long nonExistentAccountId = 99L;
            when(accountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> accountService.deactiveAccount(nonExistentAccountId))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(accountRepository, never()).save(any(Account.class));
        }
    }

}
