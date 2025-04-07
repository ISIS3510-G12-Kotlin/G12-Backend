package com.example.demo.service

import com.example.demo.model.AuthResponse
import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import com.example.demo.security.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional(readOnly = true)
    fun authenticate(email: String, password: String): AuthResponse {
        // Find user by email
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("User not found")

        // Check the password
        if (!passwordEncoder.matches(password, user.password)) {
            throw RuntimeException("Invalid credentials")
        }

        // Generate token
        val token = jwtUtil.generateToken(user)
        return AuthResponse(token)
    }

    @Transactional
    fun register(user: User): AuthResponse {
        // Check if email already exists
        if (userRepository.findByEmail(user.email) != null) {
            throw RuntimeException("Email already in use")
        }

        // Encode the password before saving
        val encodedUser = user.copy(
            password = passwordEncoder.encode(user.password),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Save the user
        val savedUser = userRepository.save(encodedUser)

        // Generate token
        val token = jwtUtil.generateToken(savedUser)
        return AuthResponse(token)
    }

    @Transactional(readOnly = true)
    fun validateToken(token: String): Boolean {
        return try {
            jwtUtil.validateToken(token)
        } catch (e: Exception) {
            false
        }
    }
}