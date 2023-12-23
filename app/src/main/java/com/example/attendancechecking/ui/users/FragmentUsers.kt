package com.example.attendancechecking.ui.users

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.attendancechecking.MyApplication
import com.example.attendancechecking.R
import com.example.attendancechecking.model.Constants.Companion.GENDER_LIST
import com.example.attendancechecking.model.Constants.Companion.ROLE_LIST
import com.example.attendancechecking.model.Constants.Companion.ROW_LIST
import com.example.attendancechecking.model.User
import com.example.attendancechecking.ui.attendance.DialogUserDetail
import com.example.attendancechecking.viewmodel.MyViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentUsers : Fragment() {
    private lateinit var viewModel: MyViewModel

    private lateinit var recView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var prevPage: ImageView
    private lateinit var nextPage: ImageView
    private lateinit var tvCurrentPage: TextView
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerRole: Spinner
    private lateinit var spinnerRow: Spinner

    private var pageIndex = 0
    private var pageSize = 10
    private var maxPages = 3

    private var currentGender: String? = null
    private var currentRole: String? = null

    private lateinit var loadingLiveData: MutableLiveData<Boolean>

    private lateinit var users: List<User>

    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_users, container, false)

        refreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        spinnerRow = view.findViewById(R.id.spinnerRows)
        spinnerGender = view.findViewById(R.id.spinnerGender)
        spinnerRole = view.findViewById(R.id.spinnerRole)
        tvCurrentPage = view.findViewById(R.id.currentPage)
        prevPage = view.findViewById(R.id.prevPage)
        nextPage = view.findViewById(R.id.nextPage)

        recView = view.findViewById(R.id.rec_view_users)
        recView.layoutManager = LinearLayoutManager(requireContext())

        listenToLoadingState()

        viewModel = (requireActivity().application as MyApplication).viewModel
        reloadUsers()

        setupSpinners()
        setupRecView()
        setupPages()

        return view
    }

    private fun listenToLoadingState() {
        loadingLiveData = MutableLiveData()
        loadingLiveData.value = false
        loadingLiveData.observe(requireActivity()) {newData ->
            view.findViewById<RelativeLayout>(R.id.waitingView).visibility = if(newData) View.VISIBLE else View.GONE
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

        val genders = GENDER_LIST
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

        val roles = ROLE_LIST
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

        val rows = ROW_LIST
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
        loadingLiveData.value = true
        GlobalScope.launch {
            with(
                viewModel.getUsers(
                    pageIndex,
                    pageSize,
                    currentGender,
                    currentRole
                )
            ) {
                users = this.first
                maxPages = this.second
                withContext(Dispatchers.Main){
                    checkPages()
                    recView.adapter = ItemAdapter(users)
                    loadingLiveData.value = false
                }
            }
        }
    }

    inner class ItemAdapter(private val itemList: List<User>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            @SuppressLint("SetTextI18n")
            fun bind(user: User) {
                itemView.findViewById<TextView>(R.id.userName).text = user.name

                itemView.findViewById<ImageView>(R.id.userAvatar)
                    .setImageResource(if (user.gender == "Male") R.drawable.male else R.drawable.female)

                itemView.findViewById<TextView>(R.id.place_and_role).text = user.role

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