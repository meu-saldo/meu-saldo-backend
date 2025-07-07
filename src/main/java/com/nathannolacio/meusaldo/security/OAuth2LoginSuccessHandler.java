package com.nathannolacio.meusaldo.security;

import com.nathannolacio.meusaldo.model.AuthProvider;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private final Logger log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final String frontendRedirectUri;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil,
                                     UserRepository userRepository,
                                     @Value("${frontend.redirect-uri}")
                                     String frontendRedirectUri) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.frontendRedirectUri = frontendRedirectUri;
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("OAuth2SuccessHandler acionado!");

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            log.info("Usuário OAuth2 recebido. Email: {}, Nome: {}", email, name);

            if (email == null) {
                log.error("Email não encontrado nos atributos do OAuth2User. Atributos disponíveis: {}", oAuth2User.getAttributes());

                response.sendRedirect(frontendRedirectUri + "? error=EmailNotFound");
                return;
            }

            log.info("Procurando ou criando usuário no banco de dados...");
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                log.info("Usuário com email {} não encontrado. Criando um novo.", email);

                User novo = new User();
                novo.setEmail(email);
                novo.setName(name);
                novo.setPassword("");
                novo.setAuthProvider(AuthProvider.GOOGLE);

                return userRepository.save(novo);
            });
            log.info("Usuário processado com sucesso. ID do usuário: {}", user.getId());

            String role = user.getRole().name();
            log.info("Role do usuário: {}", role);

            log.info("Gerando token JWT...");
            String token = jwtUtil.generateToken(email, role);
            log.info("Token JWT gerado com sucesso.");

            if (frontendRedirectUri == null || frontendRedirectUri.isEmpty()) {
                log.error("A URI de redirecionamento do frontend não está configurada! Verifique a propriedade 'frontend.redirect-uri'.");

                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Configuração de redirecionamento ausente.");
                return;
            }

            String redirectUrl = frontendRedirectUri + "?token=" + token;
            log.info("Redirecionando para: {}", redirectUrl);

            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("Erro inesperado no OAuth2SuccessHandler", e);
            response.sendRedirect(frontendRedirectUri + "?error=LoginFailed");
        }
    }
}
