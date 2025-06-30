package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.UserRequestDTO;
import com.nathannolacio.meusaldo.dto.UserResponseDTO;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO dto) {
        User user = userService.registerUser(dto);
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário deletado com sucesso.");
    }

}
