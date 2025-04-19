package com.example.demo.controllers

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import com.example.demo.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import org.slf4j.LoggerFactory

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String?,
    val lastName: String?
)

data class AuthResponse(
    val token: String,
    val user: User? = null,
    val message: String? = null
)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any?>> {
        try {
            val user = userRepository.findByEmail(loginRequest.email)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf(
                        "token" to "",
                        "message" to "Usuario no encontrado"
                    ))
            
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )
            
            SecurityContextHolder.getContext().authentication = authentication
            
            val jwt = jwtUtil.generateToken(user)
            
            // Create a sanitized user view for the response
            // This ensures we don't include sensitive data like password
            val userForResponse = mapOf(
                "id" to user.id,
                "username" to user.username,
                "email" to user.email,
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "profileImageUrl" to user.profileImageUrl,
                "createdAt" to user.createdAt,
                "updatedAt" to user.updatedAt
            )
            
            // Return both individual fields and full user data
            return ResponseEntity.ok(
                mapOf(
                    "token" to jwt,
                    "id" to user.id,
                    "email" to user.email,
                    "username" to user.username,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "userData" to userForResponse,  // Use a different key name to avoid conflicts
                    "message" to "Login successful"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf(
                    "token" to "",
                    "message" to "Error de autenticación: ${e.message}"
                ))
        }
    }
    
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Map<String, Any?>> {
        if (userRepository.existsByEmail(registerRequest.email)) {
            return ResponseEntity.badRequest().body(
                mapOf(
                    "token" to "",
                    "message" to "Email already in use"
                )
            )
        }
        
        // Create a new user from the request
        val newUser = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
            firstName = registerRequest.firstName,
            lastName = registerRequest.lastName
        )
        
        // Save the user
        val savedUser = userRepository.save(newUser)
        
        // Generate JWT
        val jwt = jwtUtil.generateToken(savedUser)
        
        // Create a sanitized user view for the response
        val userForResponse = mapOf(
            "id" to savedUser.id,
            "username" to savedUser.username,
            "email" to savedUser.email,
            "firstName" to savedUser.firstName,
            "lastName" to savedUser.lastName,
            "profileImageUrl" to savedUser.profileImageUrl,
            "createdAt" to savedUser.createdAt,
            "updatedAt" to savedUser.updatedAt
        )
        
        return ResponseEntity.ok(
            mapOf(
                "token" to jwt,
                "id" to savedUser.id,
                "email" to savedUser.email,
                "username" to savedUser.username,
                "firstName" to savedUser.firstName,
                "lastName" to savedUser.lastName,
                "userData" to userForResponse,  // Use a different key name
                "message" to "Registration successful"
            )
        )
    }
    
    // Endpoint de diagnóstico para verificar credenciales directamente
    @PostMapping("/test-auth")
    fun testAuth(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any?>> {
        val user = userRepository.findByEmail(loginRequest.email)
        
        val result = mutableMapOf<String, Any?>()
        result["userExists"] = (user != null)
        
        if (user != null) {
            val passwordMatches = passwordEncoder.matches(loginRequest.password, user.password)
            result["passwordMatches"] = passwordMatches
            result["userId"] = user.id
            result["userEmail"] = user.email
            result["storedPasswordHash"] = user.password
        }
        
        return ResponseEntity.ok(result)
    }
}