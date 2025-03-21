package com.example.demo.service

import com.example.demo.repository.UserRepository
import com.example.demo.model.AuthResponse
import com.example.demo.model.User
import com.example.demo.security.JwtUtil
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    fun authenticate(email: String, password: String): AuthResponse {
        println("Authenticating user: $email")
        
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("User not found")

        if (user.password != password) // Use password encoder in real apps!
            throw RuntimeException("Invalid credentials")

        val token = jwtUtil.generateToken(user)
        println("Authentication successful, token: $token")
        return AuthResponse(token)
    }
}