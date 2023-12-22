package com.example.attendancechecking.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.attendancechecking.data.DatabaseHelper.Companion.CHECK_IN
import com.example.attendancechecking.data.DatabaseHelper.Companion.LOGIN
import com.example.attendancechecking.model.User
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class DataRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun truncateTable(tableName: String){
        val db = databaseHelper.writableDatabase
        db.delete(tableName, null, null)
    }

    fun addLoginUser(){
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", "dainn")
            put("password", "111111")
        }
        db.insert(LOGIN, null, values)
        db.close()
    }

    fun insertData(user: User) {
//        val db = databaseHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("name", user.name)
//            put("age", user.age)
//            put("gender", user.gender)
//            put("role", user.role)
//            put("access_time", formatter.format(user.accessTime.time))
//            put("type", user.type)
//            put("place", user.place)
//        }
//        db.insert(CHECK_IN, null, values)
//        db.close()
    }

    @SuppressLint("Range")
    fun readData(gender: String? = null, role: String? = null, fromDate: Calendar, toDate: Calendar): List<User> {
        val dataList = mutableListOf<User>()
        val db = databaseHelper.readableDatabase
        val cursor =
            if(gender.isNullOrEmpty() && role.isNullOrEmpty())
                db.rawQuery("SELECT * FROM $CHECK_IN", null)
            else if(gender.isNullOrEmpty() && !role.isNullOrEmpty())
                db.rawQuery("SELECT * FROM $CHECK_IN WHERE role = \"$role\"", null)
            else if(!gender.isNullOrEmpty() && role.isNullOrEmpty())
                db.rawQuery("SELECT * FROM $CHECK_IN WHERE gender = \"$gender\"", null)
            else db.rawQuery("SELECT * FROM $CHECK_IN WHERE role = \"$role\" AND gender = \"$gender\"", null)

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val age = cursor.getInt(cursor.getColumnIndex("age"))
            val gender = cursor.getString(cursor.getColumnIndex("gender"))
            val role = cursor.getString(cursor.getColumnIndex("role"))
            val accessTimeString = cursor.getString(cursor.getColumnIndex("access_time"))
            val calendar = Calendar.getInstance()
            calendar.time = formatter.parse(accessTimeString)
            val type = cursor.getString(cursor.getColumnIndex("type"))
            val place = cursor.getString(cursor.getColumnIndex("place"))
//            dataList.add(User(name, age, gender, role, calendar, type, place))
        }

        cursor.close()
        db.close()
        val filteredList = dataList
//            .filter { user -> user.accessTime in fromDate..toDate }
        return filteredList.sortedByDescending { it.access_time }
    }

    @SuppressLint("Range")
    fun checkLogin(username: String, password: String): Int{
        //0 valid
        //1 username not exist
        //2 wrong password
        //3 unknown

        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $LOGIN WHERE username = \"$username\"", null)
        if(cursor == null) return 1
        else
            while (cursor.moveToNext()) {
                val _password = cursor.getString(cursor.getColumnIndex("password"))
                cursor.close()
                db.close()
                if(password == _password) return 0
                return 2
            }
        return 3
    }

    fun addDummyData(){
//        truncateTable(CHECK_IN)
//
//        insertData(User("Nguyen Van A", 25, "Male", "Accountant", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Trinh Van B", 30, "Male", "Security", Calendar.getInstance(), "OUT", "Storage"))
//        insertData(User("Tran Van C", 34, "Male", "Security", Calendar.getInstance(), "OUT", "Work floor 1"))
//        insertData(User("Pham Thi D", 40, "Female", "Deputy Leader", Calendar.getInstance(), "IN", "Work floor 1"))
//        insertData(User("Nguyen Thi G", 24, "Female", "Security", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Dang Cong H", 24, "Male", "Receptionist", Calendar.getInstance(), "OUT", "Work floor 2"))
//        insertData(User("Hoang Thi L", 25, "Female", "Receptionist", Calendar.getInstance(), "IN", "Work floor 3"))
//        insertData(User("Le Van M", 28, "Male", "Deputy Leader", Calendar.getInstance(), "IN", "Storage"))
//        insertData(User("Nguyen Thi N", 22, "Female", "Accountant", Calendar.getInstance(), "OUT", "Work floor 2"))
//        insertData(User("Tran Van P", 35, "Male", "Accountant", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Pham Van Q", 28, "Male", "Security", Calendar.getInstance(), "IN", "Storage"))
//        insertData(User("Do Thi R", 31, "Female", "Deputy Leader", Calendar.getInstance(), "OUT", "Work floor 3"))
//        insertData(User("Nguyen Van S", 26, "Male", "Receptionist", Calendar.getInstance(), "IN", "Work floor 3"))
//        insertData(User("Trinh Thi T", 29, "Female", "Receptionist", Calendar.getInstance(), "OUT", "Storage"))
//        insertData(User("Le Van U", 32, "Male", "Deputy Leader", Calendar.getInstance(), "IN", "Work floor 1"))
//        insertData(User("Pham Van V", 27, "Male", "Security", Calendar.getInstance(), "OUT", "Storage"))
//
//        insertData(User("Le Van M", 28, "Male", "Receptionist", Calendar.getInstance(), "IN", "Work floor 1"))
//        insertData(User("Nguyen Thi N", 22, "Female", "Receptionist", Calendar.getInstance(), "IN", "Work floor 2"))
//        insertData(User("Tran Van P", 35, "Male", "Accountant", Calendar.getInstance(), "OUT", "Meeting room"))
//        insertData(User("Pham Van Q", 28, "Male", "Security", Calendar.getInstance(), "IN", "Storage"))
//        insertData(User("Do Thi R", 31, "Female", "Security", Calendar.getInstance(), "OUT", "Work floor 2"))
//        insertData(User("Nguyen Van S", 26, "Male", "Receptionist", Calendar.getInstance(), "IN", "Work floor 2"))
//        insertData(User("Trinh Thi T", 29, "Female", "Security", Calendar.getInstance(), "OUT", "Storage"))
//        insertData(User("Le Van U", 32, "Male", "Deputy Leader", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Pham Van V", 27, "Male", "Security", Calendar.getInstance(), "OUT", "Work floor 3"))
//
//        insertData(User("Nguyen Van W", 33, "Male", "Deputy Leader", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Tran Thi X", 28, "Female", "Deputy Leader", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Hoang Van Y", 30, "Male", "Security", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Le Thi Z", 25, "Female", "Receptionist", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Pham Van A1", 34, "Male", "Accountant", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Trinh Thi B1", 26, "Female", "Security", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Nguyen Van C1", 29, "Male", "Manager", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Do Thi D1", 32, "Female", "Deputy Leader", Calendar.getInstance(), "IN", "Meeting room"))
//        insertData(User("Le Van E1", 27, "Male", "Security", Calendar.getInstance(), "IN", "Meeting room"))

    }
}
