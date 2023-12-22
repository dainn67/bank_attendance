package com.example.attendancechecking.ui.attendance

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.attendancechecking.MyApplication
import com.example.attendancechecking.R
import com.example.attendancechecking.model.User
import com.example.attendancechecking.viewmodel.MyViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class FragmentAttendance : Fragment() {
    private lateinit var viewModel: MyViewModel

    private lateinit var recView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var prevPage: ImageView
    private lateinit var nextPage: ImageView
    private lateinit var tvCurrentPage: TextView
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerRole: Spinner
    private lateinit var spinnerRow: Spinner
    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView

    private var pageIndex = 0
    private var pageSize = 10
    private var maxPages = 3
    private val selectedFromDate: Calendar = Calendar.getInstance()
    private val selectedToDate: Calendar = Calendar.getInstance()

    private var currentGender: String? = null
    private var currentRole: String? = null

    //    private lateinit var repo: DataRepository
    private lateinit var users: List<User>

    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_attendance, container, false)

        tvFromDate = view.findViewById(R.id.fromDate)
        tvToDate = view.findViewById(R.id.toDate)
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        spinnerRow = view.findViewById(R.id.spinnerRows)
        spinnerGender = view.findViewById(R.id.spinnerGender)
        spinnerRole = view.findViewById(R.id.spinnerRole)
        tvCurrentPage = view.findViewById(R.id.currentPage)
        prevPage = view.findViewById(R.id.prevPage)
        nextPage = view.findViewById(R.id.nextPage)

        recView = view.findViewById(R.id.rec_view)
        recView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = (requireActivity().application as MyApplication).viewModel
        reloadUsers()

        setupDateFilters()
        setupSpinners()
        setupRecView()
        setupPages()

        return view
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun setupDateFilters() {

        selectedFromDate.set(Calendar.DAY_OF_MONTH, 1)
        selectedToDate.set(
            Calendar.DAY_OF_MONTH,
            selectedToDate.getActualMaximum(Calendar.DAY_OF_MONTH)
        )

        tvFromDate.text = "From: ${selectedFromDate.get(Calendar.DAY_OF_MONTH)}/${
            selectedFromDate.get(Calendar.MONTH) + 1
        }/${selectedFromDate.get(Calendar.YEAR)}"
        tvToDate.text = "To: ${selectedToDate.get(Calendar.DAY_OF_MONTH)}/${
            selectedToDate.get(Calendar.MONTH) + 1
        }/${selectedToDate.get(Calendar.YEAR)}"

        view.findViewById<ImageView>(R.id.editFromDate).setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Save the selected date to the variable
                    selectedFromDate.set(year, month, dayOfMonth)

                    // Update the UI or perform any other actions with the selected date
                    tvFromDate.text =
                        "From: ${selectedFromDate.get(Calendar.DAY_OF_MONTH)}/${
                            selectedFromDate.get(Calendar.MONTH) + 1
                        }/${selectedFromDate.get(Calendar.YEAR)}"
                    reloadUsers()
                },
                selectedFromDate.get(Calendar.YEAR),
                selectedFromDate.get(Calendar.MONTH),
                selectedFromDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        view.findViewById<ImageView>(R.id.editToDate).setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Save the selected date to the variable
                    selectedToDate.set(year, month, dayOfMonth)

                    // Update the UI or perform any other actions with the selected date
                    tvToDate.text =
                        "To: ${selectedToDate.get(Calendar.DAY_OF_MONTH)}/${
                            selectedToDate.get(Calendar.MONTH) + 1
                        }/${selectedToDate.get(Calendar.YEAR)}"
                    reloadUsers()
                },
                selectedToDate.get(Calendar.YEAR),
                selectedToDate.get(Calendar.MONTH),
                selectedToDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun setupRecView() {
        reloadUsers()

        refreshLayout.setOnRefreshListener {
            reloadUsers()
            refreshLayout.isRefreshing = false
        }
    }

    private fun setupSpinners() {

        val genders = listOf("None", "Male", "Female")

        var spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = spinnerAdapter
        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentGender = when (p2) {
                    0 -> null
                    else -> genders[p2]
                }
                reloadUsers()
            }
        }

        val roles =
            listOf("None", "Accountant", "Deputy Leader", "Security", "Receptionist", "Visitor")
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = spinnerAdapter
        spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentRole = when (p2) {
                    0 -> null
                    else -> roles[p2]
                }
                reloadUsers()
            }
        }

        val rows = listOf("10", "20", "30", "All")
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rows)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRow.adapter = spinnerAdapter
        spinnerRow.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                pageSize = when (p2) {
                    0 -> 10
                    1 -> 20
                    2 -> 30
                    else -> users.size
                }
                pageIndex = 0
                tvCurrentPage.text = "Page 1"
                checkPages()
                reloadUsers()
            }
        }
    }

    private fun setupPages() {

        prevPage.setOnClickListener {
            if (pageIndex > 0) pageIndex--
            tvCurrentPage.text = "Page ${pageIndex + 1}"
            checkPages()
            reloadUsers()
        }

        nextPage.setOnClickListener {
            if (pageIndex < maxPages - 1) pageIndex++
            tvCurrentPage.text = "Page ${pageIndex + 1}"
            checkPages()
            reloadUsers()
        }
    }

    private fun checkPages() {
        if (maxPages == 1) {
            prevPage.visibility = View.GONE
            nextPage.visibility = View.GONE
        } else {
            when (pageIndex) {
                0 -> {
                    prevPage.visibility = View.GONE
                    nextPage.visibility = View.VISIBLE
                }

                maxPages - 1 -> {
                    nextPage.visibility = View.GONE
                    prevPage.visibility = View.VISIBLE
                }

                else -> {
                    nextPage.visibility = View.VISIBLE
                    prevPage.visibility = View.VISIBLE
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun reloadUsers() {
        GlobalScope.launch {
            users = viewModel.getAttendance(
                selectedFromDate,
                selectedToDate,
                pageIndex,
                pageSize,
                currentGender,
                currentRole
            )
            withContext(Dispatchers.Main) {
                recView.adapter = ItemAdapter(users)
            }
        }
    }

    inner class ItemAdapter(private val itemList: List<User>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            @SuppressLint("SetTextI18n")
            fun bind(user: User) {
                itemView.findViewById<TextView>(R.id.userName).text = user.name

                val formatter =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val time = formatter.parse(user.access_time)
                val accessTime = Calendar.getInstance()
                if (time != null) {
                    accessTime.time = time
                }

                var displayHour: String
                with(accessTime.get(Calendar.HOUR)) {
                    displayHour = if (this < 10) "0$this" else this.toString()
                }
                var displayMinute: String
                with(accessTime.get(Calendar.MINUTE)) {
                    displayMinute = if (this < 10) "0$this" else this.toString()
                }
                itemView.findViewById<TextView>(R.id.place).text = user.place
                itemView.findViewById<TextView>(R.id.userTime).text = "$displayHour:$displayMinute"
                itemView.findViewById<TextView>(R.id.userTime).setTextColor(
                    if (user.type == "Check-in") resources.getColor(
                        R.color.green
                    ) else resources.getColor(R.color.red)
                )
                itemView.findViewById<ImageView>(R.id.userAvatar)
                    .setImageResource(if (user.gender == "Male") R.drawable.male else R.drawable.female)
                itemView.findViewById<ImageView>(R.id.userDetail).setOnClickListener {
                    val dialog = DialogUserDetail(user)
                    dialog.show(requireActivity().supportFragmentManager, "detail")
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = itemList[position]
            holder.bind(currentItem)
        }

        override fun getItemCount(): Int = itemList.size
    }
}