package com.example.demo.repository

import com.example.demo.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    
    fun findByEmail(email: String): User?
    
    // Add this method to check if an email already exists
    fun existsByEmail(email: String): Boolean
}