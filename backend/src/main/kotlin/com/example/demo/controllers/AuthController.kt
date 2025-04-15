package com.example.demo.controllers

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import com.example.demo.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

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
    val user: User? = null
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
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )
            
            SecurityContextHolder.getContext().authentication = authentication
            
            val user = userRepository.findByEmail(loginRequest.email)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            // Crear versión sin password para la respuesta
            val userView = user.copy(password = null)
            
            val jwt = jwtUtil.generateToken(user)
            
            return ResponseEntity.ok(AuthResponse(token = jwt, user = userView))
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<AuthResponse> {
        if (userRepository.existsByEmail(registerRequest.email)) {
            return ResponseEntity.badRequest().build()
        }
        
        val user = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
            firstName = registerRequest.firstName,
            lastName = registerRequest.lastName
        )
        
        val savedUser = userRepository.save(user)
        
        // Crear un "user view" sin contraseña para la respuesta
        val userView = savedUser.copy(password = null)
        
        val jwt = jwtUtil.generateToken(savedUser)
        
        // Devolver el token y el usuario sin datos sensibles
        return ResponseEntity.ok(AuthResponse(token = jwt, user = userView))
    }
}