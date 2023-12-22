package com.example.attendancechecking.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.attendancechecking.MyApplication
import com.example.attendancechecking.R
import com.example.attendancechecking.data.DataRepository
import com.example.attendancechecking.viewmodel.MyViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MyViewModel

    private var isLoading = false
    private lateinit var loadingLiveData: MutableLiveData<Boolean>

    companion object {
        const val TAG = "konichiwa"
    }

    private lateinit var waitingView: RelativeLayout
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

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = (application as MyApplication).viewModel

        etAccount = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.login_submit)
        etAccountTil = findViewById(R.id.username_til)
        etPasswordTil = findViewById(R.id.password_til)

        sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        listenToLoadingState()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = sharedPreferences.getLong("timeout", 0)
        if(Calendar.getInstance() < calendar) {
            Log.i(TAG, "Still valid")
            moveToHome()
        }

        etAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etAccountTil.error = null
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
                loadingLiveData.value = true
                GlobalScope.launch {
                    val job = async { viewModel.login(account, password) }
                    val user = job.await()
                    withContext(Dispatchers.Main){
                        loadingLiveData.value = false
                    }
                    if(user != null){
                        Log.i(TAG, "HEHEHEH")
                        saveUserInfo(user.username, user.email)
                        withContext(Dispatchers.Main){
                            moveToHome()
                        }
                    }else{
                        withContext(Dispatchers.Main){
                            etPasswordTil.error = "Wrong password or invalid information"
                            etPassword.setBackgroundResource(R.drawable.et_background_red)
                        }
                    }
                }
            }
        }
    }

    private fun listenToLoadingState() {
        waitingView = findViewById(R.id.waitingView)
        loadingLiveData = MutableLiveData()
        loadingLiveData.value = isLoading
        loadingLiveData.observe(this) { newState ->
            waitingView.visibility = if (newState) View.VISIBLE else View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun moveToHome() {
        val intent = Intent(this, HomeScreen::class.java)
        startActivity(intent)
    }

    private fun saveUserInfo(account: String, email: String) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 15)
        sharedPreferences.edit().putLong("timeout", calendar.timeInMillis).apply()
        sharedPreferences.edit().putString("username", account).apply()
        sharedPreferences.edit().putString("email", email).apply()
        Log.i(TAG, "1: " + sharedPreferences.getString("username", "username empty").toString())
        Log.i(TAG, "1: " + sharedPreferences.getString("email", "email empty").toString())
    }
}