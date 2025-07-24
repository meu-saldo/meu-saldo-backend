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

    public Income add(IncomeRequestDTO dto) {
        User user = authUtils.getAuthenticatedUser();

        validateDuplicateDescription(dto.description(), user);

        Income income = new Income(
                dto.description(),
                dto.amount(),
                user
        );

        return incomeRepository.save(income);
    }

    public void delete(Long id) {
        User user = authUtils.getAuthenticatedUser();

        Income income = incomeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(IncomeNotFoundException::new);

        incomeRepository.delete(income);
    }

    private void validateDuplicateDescription(String description, User user) {
        boolean alreadyExists = incomeRepository.existsByDescriptionAndUser(description, user);

        if (alreadyExists) {
            throw new IncomeAlreadyExistsException();
        }
    }

}
