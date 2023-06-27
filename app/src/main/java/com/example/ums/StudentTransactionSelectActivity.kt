package com.example.ums

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.adapters.NewTransactionsForStudentsListItemViewAdapter
import com.example.ums.bottomsheetdialogs.TransactionAddBottomSheet
import com.example.ums.listener.ClickListener
import com.example.ums.model.databaseAccessObject.TransactionDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentTransactionSelectActivity: AppCompatActivity(), ClickListener {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var toolBar: MaterialToolbar? = null

    private var newTransactionsForStudentsListItemViewAdapter : NewTransactionsForStudentsListItemViewAdapter? = null

    private var activityName: String? = null
    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.non_searchable_list_item_select_page)
        val arguments = intent.extras
        studentID = arguments?.getInt("student_transaction_select_activity_student_id")
        activityName = arguments?.getString("student_transaction_select_activity_activity_name")

        val transactionDAO = TransactionDAO(DatabaseHelper(this))
        val professorID = studentID
        if(professorID!=null){
            newTransactionsForStudentsListItemViewAdapter = NewTransactionsForStudentsListItemViewAdapter(professorID, transactionDAO, this)
            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)

            val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            firstTextView.text = getString(R.string.no_transactions_string)
            secondTextView.text = getString(R.string.tap_add_button_to_continue_string)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = "Select Transaction"
            toolBar?.setNavigationOnClickListener {
                finish()
            }
            onRefresh()

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    finish()
                }
            })

            addFloatingActionButton.setOnClickListener {
                val transactionAddBottomSheet = TransactionAddBottomSheet()
                transactionAddBottomSheet.arguments = Bundle().apply {
                    putInt("student_id", studentID ?: return@setOnClickListener)
                }
                transactionAddBottomSheet.show(supportFragmentManager, "TransactionAddDialog")
            }

            supportFragmentManager.setFragmentResultListener("TransactionAddFragmentPosition", this){_, result->
                val position = result.getInt("id")
                newTransactionsForStudentsListItemViewAdapter?.addItem(position)
                onRefresh()
            }

            recyclerView.adapter = newTransactionsForStudentsListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onClick(bundle: Bundle?) {
        if(activityName == "StudentProfessionalCourseSelectActivity"){
            val intent = Intent(this, StudentProfessionalCourseSelectActivity::class.java)
            bundle?.putInt("student_professional_course_select_activity_student_id", studentID ?: return)
            intent.putExtras(bundle ?: return)
            resultLauncher.launch(intent)
        }
        else if(activityName == "StudentOpenCourseSelectActivity"){
            val intent = Intent(this, StudentOpenCourseSelectActivity::class.java)
            bundle?.putInt("student_professional_course_select_activity_student_id", studentID ?: return)
            intent.putExtras(bundle ?: return)
            resultLauncher.launch(intent)
        }
        onRefresh()
    }

    private fun onRefresh(){
        val transactionDAO = TransactionDAO(DatabaseHelper(this))
        if(transactionDAO.getCurrentSemesterTransactionList(studentID ?: return).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            val resultIntent = Intent()
            resultIntent.putExtras(data?.extras ?: return@registerForActivityResult)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}