package com.example.attendancechecking.model

data class ListResponse (
    val message: String,
    val data: List<User>,
    val maxPage: Int
)