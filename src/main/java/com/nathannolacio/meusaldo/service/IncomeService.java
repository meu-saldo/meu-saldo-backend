package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.IncomeRequestDTO;
import com.nathannolacio.meusaldo.dto.IncomeResponseDTO;
import com.nathannolacio.meusaldo.exception.IncomeAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.IncomeNotFoundException;
import com.nathannolacio.meusaldo.model.Income;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.IncomeRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final AuthUtils authUtils;

    public IncomeService(IncomeRepository incomeRepository,
                         AuthUtils authUtils) {
        this.incomeRepository = incomeRepository;
        this.authUtils = authUtils;
    }

    public List<IncomeResponseDTO> getUserIncomes() {
        Long userId = authUtils.getAuthenticatedUserId();

        return incomeRepository.findByUserId(userId)
                .stream()
                .map(IncomeResponseDTO::new)
                .collect(Collectors.toList());
    }

    public IncomeResponseDTO add(IncomeRequestDTO dto) {
        User user = authUtils.getAuthenticatedUser();

        validateDuplicateDescription(dto.description(), user.getId());

        Income income = new Income(
                dto.description(),
                dto.amount(),
                user
        );

        Income savedIncome = incomeRepository.save(income);

        return new IncomeResponseDTO(savedIncome);
    }

    public void delete(Long id) {
        Long userId = authUtils.getAuthenticatedUserId();

        Income income = incomeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(IncomeNotFoundException::new);

        incomeRepository.delete(income);
    }

    public Income edit(Long id, IncomeRequestDTO dto) {
        Long userId = authUtils.getAuthenticatedUserId();

        Income income = incomeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(IncomeNotFoundException::new);

        validateDuplicateDescription(dto.description(), userId, id);

        income.setDescription(dto.description());
        income.setAmount(dto.amount());

        return incomeRepository.save(income);
    }

    private void validateDuplicateDescription(String description, Long userId) {
        boolean alreadyExists = incomeRepository.existsByDescriptionAndUserId(description, userId);

        if (alreadyExists) {
            throw new IncomeAlreadyExistsException();
        }
    }

    private void validateDuplicateDescription(String description, Long userId, Long incomeId) {
        if (incomeRepository.existsByDescriptionAndUserIdAndIdNot(description, userId, incomeId)) {
            throw new IncomeAlreadyExistsException();
        }
    }

}
