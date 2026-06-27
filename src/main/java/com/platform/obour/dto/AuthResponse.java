package com.platform.obour.dto;

public record AuthResponse(
        Long id,
        String token,
        String name,
        String role
) {}