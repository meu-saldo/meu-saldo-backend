package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.ExpenseRequestDTO;
import com.nathannolacio.meusaldo.dto.ExpenseResponseDTO;
import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.Expense;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.ExpenseRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public List<ExpenseResponseDTO> findAllByUserId(Long id) {
        return expenseRepository.findByUserId(id)
                .stream()
                .map(ExpenseResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Expense add(ExpenseRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(UserNotFoundException::new);

        Expense expense = new Expense(
                dto.description(),
                dto.type(),
                dto.amount(),
                user
        );

        return expenseRepository.save(expense);
    }

}
