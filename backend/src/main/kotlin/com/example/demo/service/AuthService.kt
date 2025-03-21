package com.example.demo.service
import org.springframework.stereotype.Service
import com.example.demo.repository.UserRepository

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    fun authenticate(email: String, password: String): AuthResponse {
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("User not found")

        if (user.password != password) // Use password encoder in real apps!
            throw RuntimeException("Invalid credentials")

        val token = jwtUtil.generateToken(user)
        return AuthResponse(token)
    }
}