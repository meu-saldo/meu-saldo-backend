package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.IncomeResponseDTO;
import com.nathannolacio.meusaldo.repository.IncomeRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeService(IncomeRepository incomeRepository,
                         UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    public List<IncomeResponseDTO> getUserIncomes() {
        Long userId = AuthUtils.getAuthenticatedUserId();

        return incomeRepository.findByUserId(userId)
                .stream()
                .map(IncomeResponseDTO::new)
                .collect(Collectors.toList());
    }

}
