package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
import com.nathannolacio.meusaldo.exception.TransactionNotFounException;
import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.model.TransactionType;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.repository.TransactionRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void shouldCreteTransactionSuccessfully() {
        Long accountId = 1L;
        Long userId = 2L;
        TransactionRequestDTO dto = new TransactionRequestDTO(
                LocalDate.of(2025, 8, 10),
                "Refrigerante",
                7.0,
                TransactionType.EXPENSE,
                accountId,
                userId
        );

        Account fakeAccount = new Account();
        fakeAccount.setId(accountId);

        User fakeUser = new User();
        fakeUser.setId(userId);

        Transaction expectedTransaction = new Transaction(
                dto.date(),
                dto.description(),
                dto.amount(),
                dto.type(),
                fakeAccount,
                fakeUser
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(fakeAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        Transaction result = transactionService.createTransaction(dto);

        assertNotNull(result);
        assertEquals(dto.date(), result.getDate());
        assertEquals(dto.description(), result.getDescription());
        assertEquals(dto.amount(), result.getAmount());
        assertEquals(dto.type(), result.getType());
        assertEquals(fakeAccount, result.getAccount());
        assertEquals(fakeUser, result.getUser());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExists() {
        Long accountId = 1L;
        Long userId = 2L;
        TransactionRequestDTO dto = new TransactionRequestDTO(
                LocalDate.now(),
                "Padaria",
                15.0,
                TransactionType.EXPENSE,
                accountId,
                userId
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.createTransaction(dto);
        });

        verify(accountRepository).findById(accountId);
        verifyNoInteractions(userRepository, transactionRepository);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExists() {
        Long accountId = 1L;
        Long userId = 2L;
        TransactionRequestDTO dto = new TransactionRequestDTO(
                LocalDate.now(),
                "Mercado",
                25.0,
                TransactionType.INCOME,
                accountId,
                userId
        );

        Account fakeAccount = new Account();
        fakeAccount.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(fakeAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            transactionService.createTransaction(dto);
        });

        verify(accountRepository).findById(accountId);
        verify(userRepository).findById(userId);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldDeleteTransactionWhenExists() {
        Long id = 1L;
        Transaction transaction = new Transaction();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(id);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        Long id = 1L;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFounException.class, () -> {
            transactionService.deleteTransaction(id);
        });

        verify(transactionRepository, never()).delete(any());
    }

}
