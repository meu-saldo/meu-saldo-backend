package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.AccountRequestDTO;
import com.nathannolacio.meusaldo.dto.AccountResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.AccountNotFoundException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthUtils authUtils;

    public AccountService(AccountRepository accountRepository, AuthUtils authUtils) {
        this.accountRepository = accountRepository;
        this.authUtils = authUtils;
    }

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO dto) {
        User user = authUtils.getAuthenticatedUser();

        boolean accountAlreadyExists = accountRepository.existsByNameAndUserId(dto.name(), user.getId());

        if (accountAlreadyExists) {
            throw new AccountAlreadyExistsException();
        }

        Account account = new Account(
                dto.name(),
                dto.description(),
                BigDecimal.valueOf(0),
                user
        );

        Account savedAccount = accountRepository.save(account);

        return new AccountResponseDTO(savedAccount);
    }

    public List<AccountResponseDTO> getAllAccounts() {
        Long userId = authUtils.getAuthenticatedUserId();

        return accountRepository.findByUserId(userId)
                .stream()
                .filter(Account::isActive)
                .map(AccountResponseDTO::new)
                .toList();
    }

    public void deactivateAccount(Long accountId) {
        Long userId = authUtils.getAuthenticatedUserId();

        Account account = accountRepository.findById(accountId).
                orElseThrow(AccountNotFoundException::new);

        account.setActive(false);
        accountRepository.save(account);
    }

    public AccountResponseDTO editAccount(Long accountId, AccountRequestDTO dto) {
        Long userId = authUtils.getAuthenticatedUserId();

        Account account = accountRepository.findByIdAndUserIdAndActiveTrue(accountId, userId)
                .orElseThrow(AccountNotFoundException::new);

        account.setName(dto.name());
        account.setDescription(dto.description());

        accountRepository.save(account);

        return new AccountResponseDTO(account);
    }

}
