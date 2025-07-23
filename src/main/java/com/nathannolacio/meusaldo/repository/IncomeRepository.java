package com.nathannolacio.meusaldo.repository;

import com.nathannolacio.meusaldo.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

}
