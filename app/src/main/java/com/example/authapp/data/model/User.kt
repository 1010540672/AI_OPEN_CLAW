package com.example.authapp.data.model

data class User(
    val username: String,
    val password: String
)

data class AuthResult(
    val success: Boolean,
    val message: String,
    val user: User? = null
)
