package com.nathannolacio.meusaldo.util;

import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.UserRepository;
import com.nathannolacio.meusaldo.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    private final UserRepository userRepository;

    public AuthUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails user) {
            return user.getId();
        }

        throw new IllegalStateException("Usuário não autenticado ou tipo inválido");
    }

    public User getAuthenticatedUser() {
        Long userId = getAuthenticatedUserId();

        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

}
