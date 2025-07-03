package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.UserRequestDTO;
import com.nathannolacio.meusaldo.dto.UserResponseDTO;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> dtos = users.stream()
                .map(UserResponseDTO::new)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @Operation(summary = "Cadastra um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO dto) {
        User user = userService.registerUser(dto);
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @Operation(summary = "Deleta um usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário deletado com sucesso.");
    }

}
