package com.example.attendancechecking.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.attendancechecking.data.ApiService
import com.example.attendancechecking.data.RetrofitClient
import com.example.attendancechecking.model.User
import com.example.attendancechecking.model.UserLogin
import com.example.attendancechecking.model.UserRequest
import com.example.attendancechecking.ui.MainActivity.Companion.TAG
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import java.util.Calendar

class MyViewModel : ViewModel() {
    private val retrofit = RetrofitClient.retrofit.create(ApiService::class.java)
    private val mediaType = ("application/json").toMediaTypeOrNull()
    suspend fun getAttendance(
        fromDate: Calendar,
        toDate: Calendar,
        pageIndex: Int,
        pageSize: Int,
        gender: String?,
        role: String?
    ): List<User> {
        Log.i(TAG, "Loading lists")
        val fromDateString = "${fromDate.get(Calendar.YEAR)}-${fromDate.get(Calendar.MONTH) + 1}-${fromDate.get(Calendar.DAY_OF_MONTH)}"
        val toDateString = "${toDate.get(Calendar.YEAR)}-${toDate.get(Calendar.MONTH) + 1}-${toDate.get(Calendar.DAY_OF_MONTH)}"
        val response: Response<List<User>> = retrofit.getAttendance(
            fromDateString, toDateString, pageSize, pageIndex, gender, role
        )
        if (response.isSuccessful) {
            Log.i(TAG, "BODY: " + response.body())
            return response.body().orEmpty()
        } else{
            Log.i(TAG, response.code().toString() + " " + response.message())
        }

        return emptyList()
    }

    suspend fun login(username: String, password: String): UserLogin?{
        Log.i(TAG, "Logging in")
        val userRequest = UserRequest(username, password)
        val body = RequestBody.Companion.create(mediaType, Gson().toJson(userRequest))

        val response = retrofit.login(body)
        if(response.isSuccessful){
            Log.i(TAG, "BODY: " + response.body())
            return response.body()
        }
        return null
    }
}