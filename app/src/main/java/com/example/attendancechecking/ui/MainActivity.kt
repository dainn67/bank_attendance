package com.example.attendancechecking.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancechecking.MyApplication
import com.example.attendancechecking.R
import com.example.attendancechecking.data.DataRepository
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.exposed.sql.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var repo: DataRepository
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val TAG = "konichiwa"
    }

    private lateinit var etAccount: EditText
    private lateinit var etAccountTil: TextInputLayout
    private lateinit var etPassword: EditText
    private lateinit var etPasswordTil: TextInputLayout
    private lateinit var btnLogin: Button

    override fun onStart() {
        super.onStart()
        etAccount.text.clear()
        etPassword.text.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etAccount = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.login_submit)
        etAccountTil = findViewById(R.id.username_til)
        etPasswordTil = findViewById(R.id.password_til)

        repo = (application as MyApplication).repo
        sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = sharedPreferences.getLong("timeout", 0)
        if(Calendar.getInstance() < calendar) {
            Log.i(TAG, "Still valid")
            moveToHome()
        }

//        repo.addDummyData()
//        repo.addLoginUser()

        etAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etAccountTil.error = null
                etAccountTil.setBackgroundResource(R.drawable.et_background_red)
                etAccount.setBackgroundResource(R.drawable.et_background)
            }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etPasswordTil.error = null
                etPassword.setBackgroundResource(R.drawable.et_background)
            }
        })

        btnLogin.setOnClickListener {
            var account = etAccount.text.toString().trim()
            var password = etPassword.text.toString().trim()

            account = "dainn"
            password = "111111"

            // Reset error messages
            etAccount.error = null
            etPassword.error = null

            if (account.isEmpty()) {
                etAccountTil.error = "Account cannot be empty"
                etAccount.setBackgroundResource(R.drawable.et_background_red)
            }
            if (password.isEmpty()) {
                etPasswordTil.error = "Password cannot be empty"
                etPassword.setBackgroundResource(R.drawable.et_background_red)
            }

            if (account.isNotEmpty() && password.isNotEmpty()) {
                when (repo.checkLogin(account, password)) {
                    0 -> {
                        saveUserInfo(account)
                        moveToHome()
                    }

                    1 -> {
                        etAccountTil.error = "Username not exist"
                        etAccount.setBackgroundResource(R.drawable.et_background_red)
                    }

                    else -> {
                        etPasswordTil.error = "Wrong password or invalid information"
                        etPassword.setBackgroundResource(R.drawable.et_background_red)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun moveToHome() {
        val intent = Intent(this, HomeScreen::class.java)
        startActivity(intent)
    }

    private fun saveUserInfo(account: String) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 15)
        sharedPreferences.edit().putLong("timeout", calendar.timeInMillis).apply()
        sharedPreferences.edit().putString("username", account).apply()
    }
}