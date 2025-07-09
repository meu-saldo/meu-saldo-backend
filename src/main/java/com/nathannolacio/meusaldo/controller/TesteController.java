package com.nathannolacio.meusaldo.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return "Usuário não autenticado.";
        }
        return "Bem vindo, " + user.getAttribute("name");
    }

    @GetMapping("/api/protegido")
    public ResponseEntity<String> protegido(Authentication auth) {
        return ResponseEntity.ok("Usuário autenticado: " + auth.getName());
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

}
