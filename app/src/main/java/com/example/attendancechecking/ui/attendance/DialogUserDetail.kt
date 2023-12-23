package com.example.attendancechecking.ui.attendance

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.attendancechecking.R
import com.example.attendancechecking.model.User
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("SetTextI18n")
class DialogUserDetail(private val user: User) : DialogFragment() {

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_user_detail, null)

        val avatar = view.findViewById<ImageView>(R.id.userAvatar)
        avatar.setImageResource(if(user.gender == "Male") R.drawable.male else R.drawable.female)
        view.findViewById<TextView>(R.id.dialogUserName).text = user.name
        view.findViewById<TextView>(R.id.dialogUserAge).text = user.age.toString()
        view.findViewById<TextView>(R.id.dialogUserGender).text = user.gender
        view.findViewById<TextView>(R.id.dialogUserRole).text = user.role

        if(!user.access_time.isNullOrEmpty()){
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val time = formatter.parse(user.access_time)
            val accessTime = Calendar.getInstance()
            if (time != null) {
                accessTime.time = time
            }
            var displayHour: String
            with(accessTime.get(Calendar.HOUR)) {
                displayHour = if(this < 10) "0$this" else this.toString()
            }
            var displayMinute: String
            with(accessTime.get(Calendar.MINUTE)) {
                displayMinute = if(this < 10) "0$this" else this.toString()
            }


            view.findViewById<LinearLayout>(R.id.llAccessTime).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.llDate).visibility = View.VISIBLE

            with(view.findViewById<TextView>(R.id.dialogUserTime)) {

                this.text = "$displayHour:$displayMinute"
                this.setTextColor(if(user.type == "Check-in") resources.getColor(
                    R.color.green
                ) else resources.getColor(R.color.red))
            }
            view.findViewById<TextView>(R.id.dialogUserDate).text = "${accessTime.get(Calendar.DAY_OF_MONTH)}/${accessTime.get(Calendar.MONTH) + 1}/${accessTime.get(Calendar.YEAR)}"
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(view)

        return alertDialogBuilder.create()
    }
}