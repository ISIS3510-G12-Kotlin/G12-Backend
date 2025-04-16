package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val username: String = "",
    val email: String = "",
    
    // JsonIgnore para no enviar la contraseña en las respuestas
    @JsonIgnore
    val password: String? = null,
    
    val firstName: String? = null,
    val lastName: String? = null,
    
    val profileImageUrl: String? = null,
    
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    // Constructor por defecto necesario para JPA
    constructor() : this(0, "", "", null)
}