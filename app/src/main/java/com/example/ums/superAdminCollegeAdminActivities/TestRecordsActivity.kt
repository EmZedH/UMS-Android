package com.example.ums.superAdminCollegeAdminActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.TestListItemViewAdapter
import com.example.ums.bottomsheetdialogs.TestAddBottomSheet
import com.example.ums.bottomsheetdialogs.TestUpdateBottomSheet
import com.example.ums.dialogFragments.TestDeleteDialog
import com.example.ums.interfaces.DeleteUpdateListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TestDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TestRecordsActivity: AppCompatActivity(), DeleteUpdateListener {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var testListItemViewAdapter: TestListItemViewAdapter? = null
    private var courseID: Int? = null
    private var departmentID: Int? = null
    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.professor_list_page)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("ctest_records_is_configuration_changed")
            searchQuery = savedInstanceState.getString("ctest_records_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("ctest_records_activity_is_search_query_open")
        }
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val extras = intent.extras
        studentID = extras?.getInt("studentID")
        courseID = extras?.getInt("courseID")
        departmentID = extras?.getInt("departmentID")
        val studentID = studentID
        val departmentID = departmentID
        val courseID = courseID
        if(studentID!=null && courseID!=null && departmentID!=null){
            val databaseHelper = DatabaseHelper.newInstance(this)
            val testDAO = TestDAO(databaseHelper)
            testListItemViewAdapter = TestListItemViewAdapter(studentID, courseID, departmentID, testDAO, this)
            searchView = findViewById(R.id.search)
            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)
            val infoButton = findViewById<ActionMenuItemView>(R.id.info)

            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            onRefresh()
            recyclerView.adapter = testListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)

            infoButton.setOnClickListener {
                val intent = Intent(this, StudentCourseRecordPageActivity::class.java)
                val courseDetailsBundle = Bundle().apply {
                    putInt("student_course_record_student_id", studentID)
                    putInt("student_course_record_course_id", courseID)
                    putInt("student_course_record_department_id", departmentID)
                }
                intent.putExtras(courseDetailsBundle)
                startActivity(intent)
            }

            addFloatingActionButton.setOnClickListener {
                val testAddBottomSheet = TestAddBottomSheet.newInstance(
                    studentID, courseID, departmentID)
                testAddBottomSheet?.show(supportFragmentManager, "TestAddBottomSheetDialog")
            }
            searchView?.queryHint = getString(R.string.search)
            if(isConfigurationChanged==true){
                searchView?.isIconified = isSearchViewOpen
                isConfigurationChanged = null
            }
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    searchQuery = query
                    testListItemViewAdapter?.filter(query)
                    return false
                }
            })
            searchView?.setQuery(searchQuery, true)

            supportFragmentManager.setFragmentResultListener("TestUpdateFragmentPosition", this){_, result->
                val id = result.getInt("id")
                testListItemViewAdapter?.updateItemInAdapter(id)
                onRefresh()
            }

            supportFragmentManager.setFragmentResultListener("TestAddFragmentPosition", this){_, result->
                val id = result.getInt("id")
                testListItemViewAdapter?.addItem(id)
                onRefresh()
            }
            supportFragmentManager.setFragmentResultListener("TestDeleteDialog", this){_, result->
                val id = result.getInt("id")
                testListItemViewAdapter?.deleteItem(id)
                onRefresh()
            }

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })
        }
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

    override fun onUpdate(id: Int) {
        val testUpdateBottomSheet = TestUpdateBottomSheet.newInstance(id, studentID, courseID, departmentID)
        testUpdateBottomSheet?.show(supportFragmentManager, "TestUpdateDialog")
    }

    override fun onDelete(id: Int) {

        val testDeleteDialog = TestDeleteDialog.getInstance(id)
        testDeleteDialog.show(supportFragmentManager, "TestDeleteDialog")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("ctest_records_is_configuration_changed",true)
        outState.putString("ctest_records_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("ctest_records_activity_is_search_query_open",searchView!!.isIconified)
        }
    }

    private fun onRefresh(){
        val databaseHelper = DatabaseHelper.newInstance(this)
        val testDAO = TestDAO(databaseHelper)
        if(testDAO.getList(studentID!!, courseID!!, departmentID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_test_records_string)
            secondTextView.text = getString(R.string.tap_add_button_to_add_tests_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        val databaseHelper = DatabaseHelper.newInstance(this)
        val studentDAO = StudentDAO(databaseHelper)
        val courseDAO = CourseDAO(databaseHelper)
        toolBar?.title = "${courseDAO
            .get(courseID ?: return, 
                departmentID ?: return, 
                studentDAO.get(studentID)?.collegeID ?: return)?.name} Tests"
        testListItemViewAdapter?.updateList()
    }
}