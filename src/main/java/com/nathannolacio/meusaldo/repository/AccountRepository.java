package com.nathannolacio.meusaldo.repository;

import com.nathannolacio.meusaldo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNameAndUserId(String name, Long userId);
    List<Account> findByUserId(Long userId);
    Optional<Account> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
}
