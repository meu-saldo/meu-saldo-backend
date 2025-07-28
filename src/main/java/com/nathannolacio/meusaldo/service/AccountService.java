package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.AccountRequestDTO;
import com.nathannolacio.meusaldo.dto.AccountResponseDTO;
import com.nathannolacio.meusaldo.exception.AccountAlreadyExistsException;
import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.AccountRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

}
