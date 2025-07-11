package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
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
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
    void shouldFindAllTransactionsCreated() {
        LocalDate date = LocalDate.of(2025, 7, 11);
        User user = new User(1L, "name", "name@email.com", "password");
        Account account = new Account(1L, "Conta1");
        TransactionType type = TransactionType.EXPENSE;

        Transaction t1 = new Transaction(1L, date, "Lanche", 15.0, type, account, user);
        Transaction t2 = new Transaction(2L, date, "Salgadp", 10.0, type, account, user);

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<TransactionResponseDTO> result = transactionService.findAll();

        assertEquals(2, result.size());
        assertEquals(t1.getId(), result.get(0).id());
        assertEquals(t2.getId(), result.get(1).id());
    }

    @Test
    void shouldFindAllByUserId() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 7, 11);

        User user1 = new User(1L, "user1", "user1@email.com", "password");
        User user2 = new User(2L, "user2", "user2@email.com", "password");

        Account account = new Account(1L, "Conta1");
        TransactionType type = TransactionType.EXPENSE;

        Transaction t1 = new Transaction(1L, date, "Lanche", 100.0, type, account, user1);
        Transaction t2 = new Transaction(2L, date, "Salgadp", 150.0, type, account, user2);

        when(transactionRepository.findByUserId(userId)).thenReturn(Arrays.asList(t1, t2));

        List<TransactionResponseDTO> result = transactionService.findAllByUserId(userId);

        assertEquals(2, result.size());
        assertEquals(150.0, result.get(1).amount());
        verify(transactionRepository, times(1)).findByUserId(userId);
    }

}
