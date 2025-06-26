package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.JwtResponse;
import com.nathannolacio.meusaldo.dto.LoginRequestDTO;
import com.nathannolacio.meusaldo.security.CustomUserDetailsService;
import com.nathannolacio.meusaldo.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());

        System.out.println("Senha raw recebida: " + loginRequest.password());
        System.out.println("Senha criptografada no banco: " + userDetails.getPassword());
        System.out.println("Senha bate? " + passwordEncoder.matches(loginRequest.password(), userDetails.getPassword()));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

//        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        String jwt = jwtUtil.generateToken(userDetails);


        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Não autenticado");
        }
        return ResponseEntity.ok("Usuário autenticado: " + auth.getName() + ", Roles: " + auth.getAuthorities());
    }

}
