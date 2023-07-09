package com.example.ums.studentActivities

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.StudentTransactionsListItemViewAdapter
import com.example.ums.bottomsheetdialogs.FeePaymentBottomSheet
import com.example.ums.model.databaseAccessObject.TransactionDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class StudentTransactionPage: AppCompatActivity(){

    private var studentTransactionsListItemViewAdapter: StudentTransactionsListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null

    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.students_transaction_page_layout)
        val arguments = intent.extras
        studentID = arguments?.getInt("userID")
        val databaseHelper = DatabaseHelper.newInstance(this)
        val transactionDAO = TransactionDAO(databaseHelper)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("student_main_page_is_configuration_changed")
            searchQuery = savedInstanceState.getString("student_main_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("student_main_page_activity_is_search_query_open")
        }

        if(studentID!=null){
            val recyclerView: RecyclerView = findViewById(R.id.list_view)
            val payFeeButton = findViewById<ExtendedFloatingActionButton>(R.id.floating_action_button)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)

            studentTransactionsListItemViewAdapter = StudentTransactionsListItemViewAdapter(studentID!!, transactionDAO )


            onRefresh()

            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            toolBar.title = getString(R.string.transactions_string)
            toolBar.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }

            payFeeButton.setOnClickListener {
                if(transactionDAO.hasPaidForCurrentSemester(studentID)){
                   Toast.makeText(this, getString(R.string.already_paid_for_current_semester_string), Toast.LENGTH_SHORT).show()
                }
                else{
                    val feePaymentBottomSheet = FeePaymentBottomSheet.newInstance(studentID)
                    feePaymentBottomSheet?.show(supportFragmentManager, "FeePaymentBottomSheet")
                }
            }

            searchView = findViewById(R.id.search)
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

            searchView = findViewById(R.id.search)
            searchView?.queryHint = getString(R.string.search)
            searchView?.isIconified = isSearchViewOpen
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    searchQuery = p0
                    studentTransactionsListItemViewAdapter?.filter(p0)
                    return false
                }
            })
            if(searchQuery!=null){
                searchView?.setQuery(searchQuery, true)
            }

            supportFragmentManager.setFragmentResultListener("FeePaymentBottomSheetResult", this){_, result->
                val id = result.getInt("id")
                addAt(id)
            }

            recyclerView.adapter = studentTransactionsListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun addAt(id: Int){
        studentTransactionsListItemViewAdapter?.addItem(id)
        onRefresh()
    }

    private fun onRefresh(){
        val databaseHelper = DatabaseHelper.newInstance(this)
        val transactionDAO = TransactionDAO(databaseHelper)
        if(transactionDAO.getList(studentID ?: return).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_transactions_string)
            secondTextView.text = getString(R.string.pay_fees_to_see_past_transactions_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        studentTransactionsListItemViewAdapter?.updateList()
    }

    private fun onBack(){
        if(searchView!=null){
            if(!searchView!!.isIconified){
                searchView?.isIconified = true
                return
            }
        }
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("student_main_page_is_configuration_changed",true)
        outState.putString("student_main_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("student_main_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }
}