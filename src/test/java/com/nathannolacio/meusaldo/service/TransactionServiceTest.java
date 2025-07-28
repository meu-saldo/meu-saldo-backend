package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
import com.nathannolacio.meusaldo.exception.TransactionNotFoundException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.model.TransactionType;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.repository.TransactionRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthUtils authUtils;

    @Test
    void shouldCreateTransactionSuccessfully() {
        Long accountId = 1L;
        Long userId = 2L;
        String accountName = "Conta Corrente Principal";

        TransactionRequestDTO dto = new TransactionRequestDTO(
                LocalDate.of(2025, 8, 10),
                "Refrigerante",
                7.0,
                TransactionType.EXPENSE,
                accountId
        );

        User fakeUser = new User();
        fakeUser.setId(userId);

        Account fakeAccount = new Account();
        fakeAccount.setId(accountId);
        fakeAccount.setUser(fakeUser);
        fakeAccount.setName(accountName);

        Transaction savedTransaction = new Transaction(dto.date(), dto.description(), dto.amount(), dto.type(), fakeAccount);
        savedTransaction.setId(99L);

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(fakeAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDTO result = transactionService.createTransaction(dto);

        assertNotNull(result);
        assertEquals(savedTransaction.getId(), result.id());
        assertEquals(dto.description(), result.description());
        assertEquals(dto.amount(), result.amount());
        assertEquals(savedTransaction.getType().getLabel(), result.type());
        assertEquals(accountName, result.accountName());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenCreatingTransaction() {
        // Arrange
        Long accountId = 1L;
        Long userId = 2L;
        TransactionRequestDTO dto = new TransactionRequestDTO(
                LocalDate.now(),
                "Padaria",
                15.0,
                TransactionType.EXPENSE,
                accountId
        );

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.createTransaction(dto);
        });

        verify(accountRepository).findById(accountId);
        verifyNoInteractions(transactionRepository); // Verifica que não tentou salvar nada
    }

    @Test
    void shouldDeleteTransactionWhenExistsAndBelongsToUser() {
        Long transactionId = 1L;
        Long userId = 10L;

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByIdAndAccount_User_Id(transactionId, userId))
                .thenReturn(Optional.of(transaction));

        assertDoesNotThrow(() -> transactionService.deleteTransaction(transactionId));

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void shouldThrowTransactionNotFoundExceptionWhenDeleting() {
        Long transactionId = 1L;
        Long userId = 1L;

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByIdAndAccount_User_Id(transactionId, userId))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(transactionId);
        });

        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void shouldFindAllTransactions() {
        User user = new User(1L, "name", "name@email.com", "password");
        Account account = new Account("Conta1", "", BigDecimal.ZERO, user);

        Transaction t1 = new Transaction(LocalDate.now(), "Lanche", 15.0, TransactionType.EXPENSE, account);
        t1.setId(1L);
        Transaction t2 = new Transaction(LocalDate.now(), "Salário", 5000.0, TransactionType.INCOME, account);
        t2.setId(2L);

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<TransactionResponseDTO> result = transactionService.findAll();

        assertEquals(2, result.size());
        assertEquals(t1.getId(), result.get(0).id());
        assertEquals(t2.getId(), result.get(1).id());
        assertEquals(account.getName(), result.get(0).accountName());
        assertEquals(account.getName(), result.get(1).accountName());
    }

    @Test
    void shouldFindAllByAuthenticatedUserId() {
        Long userId = 1L;
        User user = new User(userId, "user1", "user1@email.com", "password");
        Account account = new Account("Conta do User 1", "", BigDecimal.ZERO, user);

        Transaction t1 = new Transaction(LocalDate.now(), "Lanche", 100.0, TransactionType.EXPENSE, account);
        t1.setId(1L);
        Transaction t2 = new Transaction(LocalDate.now(), "Salgado", 50.0, TransactionType.EXPENSE, account);
        t2.setId(2L);

        List<Transaction> userTransactions = Arrays.asList(t1, t2);

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByAccount_User_Id(userId)).thenReturn(userTransactions);

        List<TransactionResponseDTO> result = transactionService.findAllByUserId();

        assertEquals(2, result.size());
        assertEquals(t1.getId(), result.get(0).id());
        assertEquals(account.getName(), result.get(0).accountName());
        assertEquals(t2.getId(), result.get(1).id());
        assertEquals(account.getName(), result.get(1).accountName());

        verify(transactionRepository).findByAccount_User_Id(userId);
    }

    @Test
    void shouldEditTransactionSuccessfully() {
        Long transactionId = 1L;
        Long newAccountId = 2L;
        String newAccountName = "Conta de Investimentos";
        Long userId = 3L;

        TransactionRequestDTO dto = new TransactionRequestDTO(
                LocalDate.of(2025, 1, 1),
                "Descrição Editada",
                99.99,
                TransactionType.INCOME,
                newAccountId
        );

        User user = new User();
        user.setId(userId);

        Account newAccount = new Account();
        newAccount.setId(newAccountId);
        newAccount.setUser(user);
        newAccount.setName(newAccountName);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction updatedTransaction = invocation.getArgument(0);
            return updatedTransaction;
        });

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(accountRepository.findById(newAccountId)).thenReturn(Optional.of(newAccount));
        when(transactionRepository.findByIdAndAccount_User_Id(transactionId, userId))
                .thenReturn(Optional.of(existingTransaction));

        TransactionResponseDTO result = transactionService.editTransaction(transactionId, dto);

        assertNotNull(result);
        assertEquals(transactionId, result.id());
        assertEquals(dto.description(), result.description());
        assertEquals(dto.amount(), result.amount());
        assertEquals(dto.date(), result.date());
        assertEquals(dto.type().getLabel(), result.type());
        assertEquals(newAccountName, result.accountName());

        verify(transactionRepository).save(any(Transaction.class));
    }
}