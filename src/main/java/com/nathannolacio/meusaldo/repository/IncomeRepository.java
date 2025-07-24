package com.nathannolacio.meusaldo.repository;

import com.nathannolacio.meusaldo.model.Income;
import com.nathannolacio.meusaldo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserId(Long userId);
    boolean existsByDescriptionAndUserId(String description, Long userId);
    Optional<Income> findByIdAndUserId(Long id, Long userId);
    boolean existsByDescriptionAndUserIdAndIdNot(String description, Long userId, Long incomeId);
}
