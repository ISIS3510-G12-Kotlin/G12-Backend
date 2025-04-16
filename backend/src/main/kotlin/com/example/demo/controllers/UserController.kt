package com.example.demo.controllers

import com.example.demo.model.User
import com.example.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    // Directorio donde se guardarán las imágenes
    private val uploadDir = "uploads/profile-images"

    @GetMapping
    fun getAllUsers(): List<User> = userService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val savedUser = userService.createUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @PostMapping("/bulk")
    fun createUsers(@RequestBody users: List<User>): ResponseEntity<List<User>> {
        val savedUsers = userService.saveUsers(users)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsers)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody updatedUser: User): ResponseEntity<User> {
        val user = userService.updateUser(id, updatedUser)
        return if (user != null) {
            val userWithoutPassword = user.copy(password = "")
            ResponseEntity.ok(userWithoutPassword)
        }
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return if (userService.deleteUser(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    @PostMapping("/{id}/profile-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfileImage(
        @PathVariable id: Long,
        @RequestParam("image") file: MultipartFile
    ): ResponseEntity<User> {
        // Verificar si el usuario existe
        val user = userService.getUserById(id) ?: return ResponseEntity.notFound().build()

        try {
            // Asegurar que el directorio existe
            val directory = File(uploadDir)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Generar un nombre de archivo único
            val filename = "${UUID.randomUUID()}_${file.originalFilename?.replace(" ", "_")}"
            val path = Paths.get(uploadDir, filename)

            // Guardar el archivo
            Files.copy(file.inputStream, path)

            // Actualizar la URL de la imagen del usuario
            val imageUrl = "/api/images/profile/$filename"
            val updatedUser = user.copy(profileImageUrl = imageUrl)
            
            // Guardar el usuario actualizado
            val savedUser = userService.updateUser(id, updatedUser)
            
            return if (savedUser != null) ResponseEntity.ok(savedUser)
            else ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}