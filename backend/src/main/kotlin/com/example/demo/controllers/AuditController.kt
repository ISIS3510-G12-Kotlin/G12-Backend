package com.example.demo.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/audit")
class AuditController {

    @GetMapping
    fun getAllLogs(): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(
            listOf(
                mapOf("id" to 1, "action" to "User logged in", "timestamp" to "2025-03-21T10:00:00Z"),
                mapOf("id" to 2, "action" to "User updated profile", "timestamp" to "2025-03-21T10:05:00Z")
            )
        )
    }

    @GetMapping("/{id}")
    fun getLogById(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf("id" to id, "action" to "Fake log action", "timestamp" to "2025-03-21T11:00:00Z")
        )
    }

    @PostMapping
    fun createLog(@RequestBody payload: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("status" to "Log recorded", "data" to payload)
        )
    }

    @DeleteMapping("/{id}")
    fun deleteLog(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "Log deleted", "id" to id.toString()))
    }
}
