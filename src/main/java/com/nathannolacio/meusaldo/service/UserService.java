package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.UserRequestDTO;
import com.nathannolacio.meusaldo.exception.EmailAlreadyExistsException;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User cadastrarUsuario(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (!userExists(id)) {
            throw new IllegalArgumentException("Usuário não existe.");
        }

        userRepository.deleteById(id);
    }

    private Boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

}
