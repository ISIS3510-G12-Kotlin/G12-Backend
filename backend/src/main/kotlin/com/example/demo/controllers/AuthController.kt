package com.example.demo.controllers

import com.example.demo.service.AuthService
import com.example.demo.model.AuthRequest
import com.example.demo.model.AuthResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.authenticate(authRequest.email, authRequest.password)
            println("Authenticated, token: ${response.token}")
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            println("Login failed: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }


}