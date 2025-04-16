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
    private val logger = LoggerFactory.getLogger(AuthController::class.java)
    
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )
            
            SecurityContextHolder.getContext().authentication = authentication
            
            val user = userRepository.findByEmail(loginRequest.email)
                ?: throw RuntimeException("User not found")
            
            val jwt = jwtUtil.generateToken(user)
            
            // Crear un objeto User para la respuesta, excluyendo el password
            val userResponse = User(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                // excluir password
                profileImageUrl = user.profileImageUrl,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
            
            // Devolver el formato esperado
            return ResponseEntity.ok(AuthResponse(token = jwt, user = userResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Any> {
        logger.info("Intento de registro para: ${registerRequest.email}")
        
        if (userRepository.existsByEmail(registerRequest.email)) {
            logger.warn("Email ya registrado: ${registerRequest.email}")
            return ResponseEntity.badRequest()
                .body(mapOf("message" to "Email ya registrado"))
        }
        
        try {
            val user = User(
                username = registerRequest.username,
                email = registerRequest.email,
                password = passwordEncoder.encode(registerRequest.password),
                firstName = registerRequest.firstName,
                lastName = registerRequest.lastName
            )
            
            val savedUser = userRepository.save(user)
            logger.info("Usuario registrado exitosamente: ${savedUser.email}")
            
            // Crear copia de usuario sin contraseña
            val userResponse = savedUser.copy(password = null)
            
            val jwt = jwtUtil.generateToken(savedUser)
            
            return ResponseEntity.ok(AuthResponse(token = jwt, user = userResponse))
        } catch (e: Exception) {
            logger.error("Error durante registro para: ${registerRequest.email}", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Error en el registro", "details" to e.message))
        }
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