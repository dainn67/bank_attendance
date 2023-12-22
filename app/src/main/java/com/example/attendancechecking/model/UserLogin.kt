package com.example.attendancechecking.model

data class UserLogin (
    val username: String,
    val email: String,
)

data class UserRequest (
    val username: String,
    val password: String
)