package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.ExpenseRequestDTO;
import com.nathannolacio.meusaldo.dto.ExpenseResponseDTO;
import com.nathannolacio.meusaldo.exception.ExpenseAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.ExpenseNotFoundException;
import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.Expense;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.ExpenseRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public List<ExpenseResponseDTO> getUserExpenses() {
        Long userId = AuthUtils.getAuthenticatedUserId();

        return expenseRepository.findByUserId(userId)
                .stream()
                .map(ExpenseResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Expense add(ExpenseRequestDTO dto) {
        User user = getAuthenticatedUser();

        validateDuplicateDescription(dto.description(), user);

        Expense expense = new Expense(
                dto.description(),
                dto.type(),
                dto.amount(),
                user
        );

        return expenseRepository.save(expense);
    }

    public void delete(Long id) {
        User user = getAuthenticatedUser();

        Expense expense = expenseRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(ExpenseNotFoundException::new);

        expenseRepository.delete(expense);
    }

    public Expense edit(Long id, ExpenseRequestDTO dto) {
        User user = getAuthenticatedUser();

        Expense expense = expenseRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(ExpenseNotFoundException::new);

        validateDuplicateDescription(dto.description(), user, id);

        expense.setDescription(dto.description());
        expense.setType(dto.type());
        expense.setAmount(dto.amount());

        return expenseRepository.save(expense);
    }

    private User getAuthenticatedUser() {
        Long userId = AuthUtils.getAuthenticatedUserId();

        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validateDuplicateDescription(String description, User user) {
        boolean alreadyExists = expenseRepository.existsByDescriptionAndUser(description, user);

        if (alreadyExists) {
            throw new ExpenseAlreadyExistsException();
        }
    }

    private void validateDuplicateDescription(String description, User user, Long expenseToIgnore) {
        if (expenseRepository.existsByDescriptionAndUserAndIdNot(description, user, expenseToIgnore)) {
            throw new ExpenseAlreadyExistsException();
        }
    }

}
