package com.example.demo.controllers

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.file.Paths

@RestController
@RequestMapping("/api/images")
class ImageController {

    private val uploadDir = "uploads/profile-images"

    @GetMapping("/profile/{filename}", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE])
    fun getProfileImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = File(Paths.get(uploadDir, filename).toString())
        
        return if (file.exists()) {
            ResponseEntity.ok(FileSystemResource(file))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}