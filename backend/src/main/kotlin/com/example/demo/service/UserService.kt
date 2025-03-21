package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: Long): User? = userRepository.findById(id).orElse(null)

    fun createUser(user: User): User = userRepository.save(user)

    fun saveUsers(users: List<User>): List<User> {
        return userRepository.saveAll(users)
    }

    fun updateUser(id: Long, updatedUser: User): User? {
        val existingUser = userRepository.findById(id).orElse(null) ?: return null
        val newUser = existingUser.copy(name = updatedUser.name, email = updatedUser.email, password = updatedUser.password,
        role = updatedUser.role)
        return userRepository.save(newUser)
    }

    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
