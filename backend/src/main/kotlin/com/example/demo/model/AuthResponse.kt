package com.example.demo.model

data class AuthResponse(
    val token: String,
    val user: User? = null,
    val message: String? = null
)