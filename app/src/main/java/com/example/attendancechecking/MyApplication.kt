package com.example.attendancechecking

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.attendancechecking.data.DataRepository

@RequiresApi(Build.VERSION_CODES.O)
class MyApplication: Application() {
    val repo: DataRepository by lazy {
        DataRepository(this.applicationContext)
    }
}