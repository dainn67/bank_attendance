package com.example.attendancechecking.model

class Constants {
    companion object {
        val GENDER_LIST = listOf("None", "Male", "Female")
        val ROLE_LIST = listOf("None", "Accountant", "Deputy Leader", "Security", "Receptionist", "Customer")
        val ROW_LIST = listOf("10", "20", "30", "All")
        const val DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
}