package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.User;

public record UserResponseDTO(Long id, String nome, String email) {
    public UserResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
