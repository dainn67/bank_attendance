package com.example.attendancechecking.model

data class User(
    val name: String,
    val age: Int,
    val gender: String,
    val role: String,
    val access_time: String? = null,
    val type: String? = null,
    val place: String? = null
)
