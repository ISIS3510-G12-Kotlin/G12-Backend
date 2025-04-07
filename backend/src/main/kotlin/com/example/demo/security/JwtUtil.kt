package com.example.demo.security

import com.example.demo.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {
    
    @Value("\${jwt.secret:defaultSecretKeyWhichShouldBeAtLeast32CharactersLong}")
    private lateinit var secret: String
    
    @Value("\${jwt.expiration:86400000}") // 24 hours in milliseconds
    private var expirationTime: Long = 0
    
    fun generateToken(user: User): String {
        val now = Date()
        val expiration = Date(now.time + expirationTime)
        
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        
        return Jwts.builder()
            .setSubject(user.email)
            .claim("id", user.id)
            .claim("username", user.username)
            // Remove the role claim since your User class doesn't have a role
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
    
    fun validateToken(token: String): Boolean {
        try {
            val key = Keys.hmacShaKeyFor(secret.toByteArray())
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    fun getEmailFromToken(token: String): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        
        return claims.subject
    }
}