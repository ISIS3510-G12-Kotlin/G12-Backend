package com.example.demo.service

import com.example.demo.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        // Using null check instead of orElseThrow since your repository returns User?
        val user = userRepository.findByEmail(username) 
            ?: throw UsernameNotFoundException("User not found with email: $username")
        
        // Create authorities - you can adapt this based on your user roles
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        
        // Create and return Spring Security's UserDetails
        return User(
            user.email,
            user.password,
            authorities
        )
    }
}