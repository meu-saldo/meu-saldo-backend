package com.nathannolacio.meusaldo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "O nome é um campo obrigatório")
        @Size(min = 5, message = "O nome deve ter pelo menos 5 caracteres")
        String name,

        @NotBlank(message = "O email é um campo obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotBlank(message = "A senha é um campo obrigatório")
        String password) {
}
