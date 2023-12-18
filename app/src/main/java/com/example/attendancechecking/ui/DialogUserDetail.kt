package com.example.attendancechecking.ui


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.attendancechecking.R
import com.example.attendancechecking.data.User
import java.util.Calendar

@SuppressLint("SetTextI18n")
class DialogUserDetail(private val user: User) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_user_detail, null)

//        val avatar = view.findViewById<ImageView>(R.id.userAvatar)
        view.findViewById<TextView>(R.id.dialogUserName).text = user.name
        view.findViewById<TextView>(R.id.dialogUserAge).text = user.age.toString()
        view.findViewById<TextView>(R.id.dialogUserGender).text = user.gender
        view.findViewById<TextView>(R.id.dialogUserRole).text = user.role
        view.findViewById<TextView>(R.id.dialogUserTime).text = "${user.accessTime.get(Calendar.HOUR)}:${user.accessTime.get(Calendar.MINUTE)}"
        view.findViewById<TextView>(R.id.dialogUserTime).setTextColor(if(user.type == "IN") resources.getColor(
            R.color.green
        ) else resources.getColor(R.color.red))
        view.findViewById<TextView>(R.id.dialogUserDate).text = "${user.accessTime.get(Calendar.DAY_OF_MONTH)}/${user.accessTime.get(Calendar.MONTH) + 1}/${user.accessTime.get(Calendar.YEAR)}"

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(view)

        return alertDialogBuilder.create()
    }
}
