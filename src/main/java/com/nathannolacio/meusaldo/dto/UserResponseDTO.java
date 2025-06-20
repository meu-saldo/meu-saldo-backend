package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDTO(
        @Schema(description = "User name", example = "Nathan Nolacio")
        String nome,

        @Schema(description = "User email", example = "nathan@gmail.com")
        String email)
{
    public UserResponseDTO(User user) {
        this(user.getName(), user.getEmail());
    }
}
