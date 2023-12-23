package com.example.attendancechecking.data

import com.example.attendancechecking.model.ListResponse
import com.example.attendancechecking.model.User
import com.example.attendancechecking.model.UserLogin
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("api/attendance/")
    suspend fun getAttendance(
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageIndex") pageIndex: Int,
        @Query("gender") gender: String? = null,
        @Query("role") role: String? = null,
    ): Response<ListResponse>

    @POST("api/login/")
    suspend fun login(
        @Body body: RequestBody,
    ): Response<UserLogin>

    @GET("api/users/")
    suspend fun getUsers(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("gender") gender: String? = null,
        @Query("role") role: String? = null
    ): Response<ListResponse>
}