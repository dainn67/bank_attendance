package com.example.attendancechecking

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.attendancechecking.viewmodel.MyViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MyApplication: Application() {
    val viewModel: MyViewModel by lazy {
        MyViewModel()
    }
}