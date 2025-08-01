package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
import com.nathannolacio.meusaldo.exception.TransactionNotFoundException;
import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.model.TransactionType;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.repository.TransactionRepository;
import com.nathannolacio.meusaldo.repository.UserRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuthUtils authUtils;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              AuthUtils authUtils) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.authUtils = authUtils;
    }

    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> findAllByUserId() {
        Long userId = authUtils.getAuthenticatedUserId();

        return transactionRepository.findByAccount_User_Id(userId)
                .stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        Long userId = authUtils.getAuthenticatedUserId();
        Account account = accountRepository.findByIdAndUserIdAndActiveTrue(dto.accountId(), userId)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal amountWithSign = dto.type() == TransactionType.EXPENSE ? dto.amount().negate() : dto.amount();
        BigDecimal newBalance = account.getBalance().add(amountWithSign);

        account.setBalance(newBalance);

        Transaction transaction = new Transaction(
                dto.date(),
                dto.description(),
                amountWithSign,
                dto.type(),
                account
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        return new TransactionResponseDTO(savedTransaction);
    }

    public void deleteTransaction(Long transactionId) {
        Long userId = authUtils.getAuthenticatedUserId();

        Transaction transaction = transactionRepository.findByIdAndAccount_User_Id(transactionId, userId)
                .orElseThrow(TransactionNotFoundException::new);

        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO editTransaction(Long id, TransactionRequestDTO dto) {
        Long userId = authUtils.getAuthenticatedUserId();

        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(AccountNotFoundException::new);

        Transaction transaction = transactionRepository.findByIdAndAccount_User_Id(id, userId)
                .orElseThrow(TransactionNotFoundException::new);

        transaction.setDescription(dto.description());
        transaction.setDate(dto.date());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setAccount(account);

        Transaction udpdatedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDTO(udpdatedTransaction);
    }

}
