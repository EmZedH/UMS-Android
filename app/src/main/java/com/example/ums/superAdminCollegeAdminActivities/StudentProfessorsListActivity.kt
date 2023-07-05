package com.example.ums.superAdminCollegeAdminActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.StudentProfessorsListItemViewAdapter
import com.example.ums.interfaces.ClickListener
import com.example.ums.interfaces.Searchable
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentProfessorsListActivity: AppCompatActivity(), ClickListener, Searchable {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var toolBar: MaterialToolbar? = null

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true

    private var newProfessorsForCoursesListItemViewAdapter : StudentProfessorsListItemViewAdapter? = null

    private var transactionID: Int? = null
    private var collegeID: Int? = null
    private var departmentID: Int? = null
    private var courseID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_select_page)
        val arguments = intent.extras
        transactionID = arguments?.getInt("transaction_id")
        departmentID = arguments?.getInt("course_professor_list_activity_department_id")
        collegeID = arguments?.getInt("course_professor_list_activity_college_id")
        courseID = arguments?.getInt("course_professor_list_activity_course_id")

        if(savedInstanceState!=null){
            searchQuery = savedInstanceState.getString("course_professor_list_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("course_professor_list_activity_is_search_query_open")
        }

        if(courseID!=null && departmentID !=null && collegeID!=null){
            val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(this))
            newProfessorsForCoursesListItemViewAdapter = StudentProfessorsListItemViewAdapter(courseID!!, departmentID!!, collegeID!!, courseProfessorDAO, this)
            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)

            val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)

            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = "Select Professors"
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            onRefresh()

            initializeSearchView()


            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

            addFloatingActionButton.setOnClickListener {
                val intent = Intent(this, ProfessorSelectForCourseActivity::class.java)
                val bundle = Bundle().apply {
                    putInt("course_professor_list_activity_department_id", departmentID ?: return@setOnClickListener)
                    putInt("course_professor_list_activity_college_id", collegeID ?: return@setOnClickListener)
                    putInt("course_professor_list_activity_course_id", courseID ?: return@setOnClickListener)
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }

            supportFragmentManager.setFragmentResultListener("ProfessorAddFragmentPosition", this){_, result->
                val position = result.getInt("id")
                newProfessorsForCoursesListItemViewAdapter?.addItem(position)
                onRefresh()
            }

            recyclerView.adapter = newProfessorsForCoursesListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun initializeSearchView() {
        searchView = findViewById(R.id.search)
        searchView?.queryHint = getString(R.string.search)
        searchView?.isIconified = isSearchViewOpen
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchQuery = p0
                onSearch(p0)
                return false
            }
        })
        if(searchQuery!=null){
            searchView?.setQuery(searchQuery, true)
        }
    }

    override fun onClick(bundle: Bundle?) {
        val resultIntent = Intent()
        bundle?.putInt("transaction_id", transactionID ?: return)
        resultIntent.putExtras(bundle ?: return)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun onRefresh(){
        val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(this))

        if(courseProfessorDAO.getList(courseID ?: return, departmentID ?: return, collegeID ?: return).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_professor_string)
            secondTextView.text = getString(R.string.tap_add_button_to_continue_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onSearch(query: String?) {
        newProfessorsForCoursesListItemViewAdapter?.filter(query)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("course_professor_list_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("course_professor_list_activity_is_search_query_open",searchView!!.isIconified)
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

    override fun onResume() {
        super.onResume()
        onRefresh()
        newProfessorsForCoursesListItemViewAdapter?.updateList()
    }
}