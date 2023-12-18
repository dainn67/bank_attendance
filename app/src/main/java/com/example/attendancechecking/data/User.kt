package com.example.attendancechecking.data

import java.time.LocalDateTime
import java.util.Calendar

data class User(
    val name: String,
    val age: Int,
    val gender: String,
    val role: String,
    val accessTime: Calendar,
    val type: String,
    val place: String
)
