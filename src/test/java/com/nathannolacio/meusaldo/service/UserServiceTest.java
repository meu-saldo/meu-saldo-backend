package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.UserRequestDTO;
import com.nathannolacio.meusaldo.dto.UserResponseDTO;
import com.nathannolacio.meusaldo.exception.EmailAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.UserNotFoundException;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        UserRequestDTO dto = new UserRequestDTO("Nathan", "nathan@email.com", "123456");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("senhaCodificada");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerUser(dto);

        assertEquals("Nathan", user.getName());
        assertEquals("nathan@email.com", user.getEmail());
        assertEquals("senhaCodificada", user.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRequestDTO dto = new UserRequestDTO("Nathan", "nathan@email.com", "123456");

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.registerUser(dto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotFound() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUserById(userId);
        });

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void shouldReturnListOfUserResponseDTOs() {
        User user1 = new User(1L, "Maria Silva", "maria@example.com", "senha1");
        User user2 = new User(2L, "João Souza", "joao@example.com", "senha2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(user1, user2);
    }
}