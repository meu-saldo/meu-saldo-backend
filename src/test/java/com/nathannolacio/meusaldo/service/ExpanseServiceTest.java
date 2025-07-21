package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.ExpenseRequestDTO;
import com.nathannolacio.meusaldo.dto.ExpenseResponseDTO;
import com.nathannolacio.meusaldo.exception.ExpenseNotFoundException;
import com.nathannolacio.meusaldo.model.Expense;
import com.nathannolacio.meusaldo.model.ExpenseType;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.ExpenseRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpanseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    private MockedStatic<AuthUtils> authUtils;

    private final Long userId = 1L;

    @BeforeEach
    void setup() {
        authUtils = Mockito.mockStatic(AuthUtils.class);
        authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        authUtils.close();
    }

    @Test
    void shouldReturnListOfUserExpenses() {
        Expense expense1 = new Expense("Aluguel", ExpenseType.ESSENTIAL,1000.0, null);
        expense1.setId(1L);
        Expense expense2 = new Expense("Mercado", ExpenseType.ESSENTIAL, 500.0, null);
        expense2.setId(2L);

        when(expenseRepository.findByUserId(userId)).thenReturn(List.of(expense1, expense2));

        List<ExpenseResponseDTO> result = expenseService.getUserExpenses();

        assertEquals(2, result.size());
        assertEquals("Aluguel", result.get(0).description());
        assertEquals("Mercado", result.get(1).description());
    }

    @Test
    void shouldAddNewExpense() {
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Netflix", ExpenseType.NOT_ESSENTIAL, 50.0);
        User user = new User();
        user.setId(userId);

        Expense savedExpense = new Expense(dto.description(), dto.type(), dto.amount(), user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Expense result = expenseService.add(dto);

        assertEquals("Netflix", result.getDescription());
        assertEquals(ExpenseType.NOT_ESSENTIAL, result.getType());
        assertEquals(50.0, result.getAmount());
        assertEquals(user, result.getUser());
    }

    @Test
    void shouldDeleteUserExpense() {
        Expense expense = new Expense("Gasolina", ExpenseType.NOT_ESSENTIAL, 200.0, null);
        expense.setId(3L);

        when(expenseRepository.findByIdAndUserId(3L, userId)).thenReturn(Optional.of(expense));

        expenseService.delete(3L);

        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentExpense() {
        when(expenseRepository.findByIdAndUserId(99L, userId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.delete(99L));
    }

    @Test
    void shouldEditExpenseSuccessfully() {
        Expense existing = new Expense("Conta antiga", ExpenseType.ESSENTIAL, 100.0, null);
        existing.setId(5L);

        ExpenseRequestDTO dto = new ExpenseRequestDTO("Conta nova", ExpenseType.ESSENTIAL, 150.0);

        when(expenseRepository.findByIdAndUserId(5L, userId)).thenReturn(Optional.of(existing));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));

        Expense updated = expenseService.edit(5L, dto);

        assertEquals("Conta nova", updated.getDescription());
        assertEquals(ExpenseType.ESSENTIAL, updated.getType());
        assertEquals(150.0, updated.getAmount());
    }

    @Test
    void shouldThrowWhenEditingNonexistentExpense() {
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Luz", ExpenseType.ESSENTIAL, 300.0);

        when(expenseRepository.findByIdAndUserId(10L, userId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.edit(10L, dto));
    }

}
