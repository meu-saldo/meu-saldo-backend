package com.nathannolacio.meusaldo.util;

import com.nathannolacio.meusaldo.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails user) {
            return user.getId();
        }

        throw new IllegalStateException("Usuário não autenticado ou tipo inválido");
    }

}
