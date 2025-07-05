package com.nathannolacio.meusaldo.security;

import com.nathannolacio.meusaldo.model.AuthProvider;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil,
                                     UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
           User novo = new User();
           novo.setEmail(email);
           novo.setName(name);
           novo.setPassword("");
           novo.setAuthProvider(AuthProvider.GOOGLE);

           return userRepository.save(novo);
        });

        String token = jwtUtil.generateToken(email);

        String redirectUrl = "http://localhost:3000/logado?token=" + token;
        response.sendRedirect(redirectUrl);
    }

}
