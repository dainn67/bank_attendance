package com.example.attendancechecking.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.attendancechecking.R
import com.example.attendancechecking.ui.attendance.FragmentAttendance
import com.example.attendancechecking.ui.users.FragmentUsers
import com.google.android.material.navigation.NavigationView

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n")
class HomeScreen : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var fragsContainer: FragmentContainerView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var navController: NavController
    private lateinit var appbarConfiguration: AppBarConfiguration

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = this@HomeScreen.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.nav_view)
        fragsContainer = findViewById(R.id.frags_container)

        setSupportActionBar(toolbar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        val navHeader = navView.getHeaderView(0)

        navHeader.findViewById<TextView>(R.id.textViewUsername).text = sharedPreferences.getString("username", "user1")
        navHeader.findViewById<TextView>(R.id.textViewEmail).text = sharedPreferences.getString("email", "user1@gmail.com")

        navController = findNavController(R.id.host_fragment)
        appbarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_menu_attendance,
                R.id.nav_menu_user
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appbarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_menu_attendance -> {
                    findViewById<TextView>(R.id.title).text = "Attendance"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frags_container, FragmentAttendance())
                        commit()
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_menu_user -> {
                    findViewById<TextView>(R.id.title).text = "Users"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frags_container, FragmentUsers())
                        commit()
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_menu_logout -> {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    sharedPreferences.edit().clear().apply()
                    onBackPressed()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

        //screens
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frags_container, FragmentAttendance())
            commit()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(navView)
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}