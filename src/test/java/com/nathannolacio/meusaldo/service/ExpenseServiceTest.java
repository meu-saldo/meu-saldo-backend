package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.ExpenseRequestDTO;
import com.nathannolacio.meusaldo.dto.ExpenseResponseDTO;
import com.nathannolacio.meusaldo.exception.ExpenseAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.ExpenseNotFoundException;
import com.nathannolacio.meusaldo.model.Expense;
import com.nathannolacio.meusaldo.model.ExpenseType;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.ExpenseRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private AuthUtils authUtils;

    private final Long userId = 1L;
    private User authenticatedUser;

    @BeforeEach
    void setup() {
        expenseService = new ExpenseService(expenseRepository, authUtils);

        authenticatedUser = new User();
        authenticatedUser.setId(userId);
        authenticatedUser.setName("Test User");

        when(authUtils.getAuthenticatedUserId()).thenReturn(userId);
        when(authUtils.getAuthenticatedUser()).thenReturn(authenticatedUser);
    }

    @Test
    @DisplayName("Deve retornar a lista de despesas do usuário autenticado")
    void getUserExpenses_shouldReturnListOfUserExpenses() {
        Expense expense1 = new Expense("Aluguel", ExpenseType.ESSENTIAL, BigDecimal.valueOf(1000.0), authenticatedUser);
        Expense expense2 = new Expense("Mercado", ExpenseType.ESSENTIAL, BigDecimal.valueOf(500.0), authenticatedUser);
        List<Expense> expensesFromRepo = List.of(expense1, expense2);

        when(expenseRepository.findByUserId(userId)).thenReturn(expensesFromRepo);

        List<ExpenseResponseDTO> result = expenseService.getUserExpenses();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(expenseRepository).findByUserId(userId);
        verify(authUtils).getAuthenticatedUserId();
    }

    @Test
    @DisplayName("Deve adicionar uma nova despesa com sucesso")
    void add_shouldAddNewExpenseSuccessfully() {
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Netflix", ExpenseType.NOT_ESSENTIAL, BigDecimal.valueOf(50.0));

        when(expenseRepository.existsByDescriptionAndUser(dto.description(), authenticatedUser)).thenReturn(false);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Expense result = expenseService.add(dto);

        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(expenseCaptor.capture());

        Expense savedExpense = expenseCaptor.getValue();

        assertNotNull(result);
        assertEquals(dto.description(), savedExpense.getDescription());
        assertEquals(dto.type(), savedExpense.getType());
        assertEquals(dto.amount(), savedExpense.getAmount());
        assertEquals(authenticatedUser, savedExpense.getUser());

        verify(authUtils).getAuthenticatedUser();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar despesa com descrição duplicada")
    void add_shouldThrowWhenDescriptionAlreadyExists() {
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Aluguel", ExpenseType.ESSENTIAL, BigDecimal.valueOf(1500.0));

        when(expenseRepository.existsByDescriptionAndUser(dto.description(), authenticatedUser)).thenReturn(true);

        assertThrows(ExpenseAlreadyExistsException.class, () -> {
            expenseService.add(dto);
        });

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve deletar uma despesa existente do usuário")
    void delete_shouldDeleteUserExpense() {
        Long expenseId = 3L;
        Expense expenseToDelete = new Expense("Gasolina", ExpenseType.NOT_ESSENTIAL, BigDecimal.valueOf(200.0), authenticatedUser);

        when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.of(expenseToDelete));
        doNothing().when(expenseRepository).delete(any(Expense.class));

        assertDoesNotThrow(() -> {
            expenseService.delete(expenseId);
        });

        verify(expenseRepository).delete(expenseToDelete);
        verify(authUtils).getAuthenticatedUser();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar despesa inexistente")
    void delete_shouldThrowWhenDeletingNonexistentExpense() {
        Long nonExistingExpenseId = 99L;

        when(expenseRepository.findByIdAndUserId(nonExistingExpenseId, userId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.delete(nonExistingExpenseId);
        });

        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    @DisplayName("Deve editar uma despesa com sucesso")
    void edit_shouldEditExpenseSuccessfully() {
        Long expenseId = 5L;
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Conta de Luz", ExpenseType.ESSENTIAL, BigDecimal.valueOf(150.0));

        Expense existingExpense = new Expense("Conta antiga", ExpenseType.ESSENTIAL, BigDecimal.valueOf(100.0), authenticatedUser);
        existingExpense.setId(expenseId);

        when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.existsByDescriptionAndUserAndIdNot(dto.description(), authenticatedUser, expenseId)).thenReturn(false);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense updatedExpense = expenseService.edit(expenseId, dto);

        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(expenseCaptor.capture());
        Expense savedExpense = expenseCaptor.getValue();

        assertEquals(expenseId, savedExpense.getId()); // O ID não deve mudar
        assertEquals("Conta de Luz", savedExpense.getDescription());
        assertEquals(BigDecimal.valueOf(150.0), savedExpense.getAmount());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar editar despesa com descrição duplicada")
    void edit_shouldThrowWhenDescriptionAlreadyExistsOnAnotherExpense() {
        Long expenseIdToEdit = 5L;
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Cinema", ExpenseType.NOT_ESSENTIAL, BigDecimal.valueOf(80.0));

        Expense existingExpense = new Expense("Conta antiga", ExpenseType.ESSENTIAL, BigDecimal.valueOf(100.0), authenticatedUser);
        existingExpense.setId(expenseIdToEdit);

        when(expenseRepository.findByIdAndUserId(expenseIdToEdit, userId)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.existsByDescriptionAndUserAndIdNot(dto.description(), authenticatedUser, expenseIdToEdit)).thenReturn(true);

        assertThrows(ExpenseAlreadyExistsException.class, () -> {
            expenseService.edit(expenseIdToEdit, dto);
        });

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar editar despesa inexistente")
    void edit_shouldThrowWhenEditingNonexistentExpense() {
        Long nonExistingExpenseId = 10L;
        ExpenseRequestDTO dto = new ExpenseRequestDTO("Luz", ExpenseType.ESSENTIAL, BigDecimal.valueOf(300.0));

        when(expenseRepository.findByIdAndUserId(nonExistingExpenseId, userId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.edit(nonExistingExpenseId, dto);
        });

        verify(expenseRepository, never()).save(any());
    }
}