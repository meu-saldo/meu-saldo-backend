package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.User;

public record UserResponseDTO(String nome, String email) {
    public UserResponseDTO(User user) {
        this(user.getName(), user.getEmail());
    }
}
