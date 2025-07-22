package com.nathannolacio.meusaldo.repository;

import com.nathannolacio.meusaldo.model.Expense;
import com.nathannolacio.meusaldo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    Optional<Expense> findByIdAndUserId(Long id, Long userId);
    boolean existsByDescriptionAndUser(String description, User user);
    boolean existsByDescriptionAndUserAndIdNot(String description, User user, Long expenseIdToIgnore);
}
