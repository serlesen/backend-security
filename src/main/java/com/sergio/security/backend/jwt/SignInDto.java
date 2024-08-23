package com.sergio.security.backend.jwt;

public record SignInDto(String username, char[] password) {
}
