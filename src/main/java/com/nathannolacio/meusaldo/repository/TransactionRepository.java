package com.nathannolacio.meusaldo.repository;

import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_User_Id(Long userId);
    Optional<Transaction> findByIdAndAccount_User_Id(Long id, Long userId);
}
