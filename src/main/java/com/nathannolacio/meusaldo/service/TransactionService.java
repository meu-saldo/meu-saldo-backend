package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.repository.TransactionRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> findAllByUserId(Long id) {
        return transactionRepository.findByUserId(id)
                .stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Transaction createTransaction(TransactionRequestDTO dto) {
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(AccountNotFoundException::new);

        User user = userRepository.findById(dto.userId())
                .orElseThrow(UserNotFoundException::new);

        Transaction transaction = new Transaction(
                dto.date(),
                dto.description(),
                dto.amount(),
                dto.type(),
                account,
                user
        );

        return transactionRepository.save(transaction);
    }

}
