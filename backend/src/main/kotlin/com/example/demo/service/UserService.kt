package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun createUser(user: User): User = userRepository.save(user)

    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)

    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> = userRepository.findAll()

    @Transactional(readOnly = true)
    fun getUserById(id: Long): User? = userRepository.findById(id).orElse(null)

    @Transactional
    fun saveUsers(users: List<User>): List<User> = userRepository.saveAll(users)

    @Transactional
    fun updateUser(id: Long, updatedUser: User): User? {
        return if (userRepository.existsById(id)) {
            val user = updatedUser.copy(id = id)
            userRepository.save(user)
        } else {
            null
        }
    }

    @Transactional
    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
