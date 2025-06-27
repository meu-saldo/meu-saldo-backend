package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.ErrorResponse;
import com.nathannolacio.meusaldo.dto.JwtResponse;
import com.nathannolacio.meusaldo.dto.LoginRequestDTO;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.security.CustomUserDetails;
import com.nathannolacio.meusaldo.security.CustomUserDetailsService;
import com.nathannolacio.meusaldo.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldReturnJwtTokenWhenCredentialsAreValid() {
        String email = "nathan@example.com";
        String rawPassword = "securePassword123";
        String encodedPassword = "encodedPass";
        String expectedToken = "fake-jwt-token";

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, rawPassword);

        Authentication authentication = mock(Authentication.class);
        User user = new User(1L, "Nathan Nolacio", email, encodedPassword);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(expectedToken);

        ResponseEntity<?> response = authController.login(loginRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(JwtResponse.class, response.getBody());

        JwtResponse jwt = (JwtResponse) response.getBody();
        assertEquals(expectedToken, jwt.token());
    }

    @Test
    void shouldReturnErrorResponseWhenLoginFails() {
        String email = "nathan@example.com";
        String password = "wrong@Password";

        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals(401, error.status());
        assertEquals("Credenciais inválidas", error.message());
        assertEquals("Unauthorized", error.error());
        assertNotNull(error.timestamp());
    }
}
