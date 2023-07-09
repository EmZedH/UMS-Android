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
import com.example.ums.adapters.CourseStudentListItemViewAdapter
import com.example.ums.interfaces.ClickListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.google.android.material.appbar.MaterialToolbar

class CourseStudentsListActivity: AppCompatActivity(), ClickListener {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var courseStudentListItemViewAdapter: CourseStudentListItemViewAdapter? = null
    private var onClickActivity: String? = null

    private var professorID: Int? = null
    private var courseID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_layout_with_info_without_fab_layout)

        if(savedInstanceState!=null){
            onClickActivity = savedInstanceState.getString("on_click_activity")
            isConfigurationChanged = savedInstanceState.getBoolean("course_professor_is_configuration_changed")
            searchQuery = savedInstanceState.getString("course_professor_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("course_professor_activity_is_search_query_open")
        }
        val bundle = intent.extras
        professorID = bundle?.getInt("professor_id")
        courseID = bundle?.getInt("course_id")
        val courseID = courseID
        val professorID = professorID
        if(professorID!=null && courseID!=null){
            val databaseHelper = DatabaseHelper.newInstance(this)
            val studentDAO = StudentDAO(databaseHelper)
            val professorDAO = ProfessorDAO(databaseHelper)
            courseStudentListItemViewAdapter = CourseStudentListItemViewAdapter(professorID, courseID, studentDAO, professorDAO, this)
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
            recyclerView.adapter = courseStudentListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)

            infoButton.setOnClickListener {
                val professor = professorDAO.get(professorID)
                val intent = Intent(this, CourseDetailsActivity::class.java)
                val courseDetailsBundle = Bundle().apply {
                    putInt("course_details_college_id", professor?.collegeID ?: return@setOnClickListener)
                    putInt("course_details_department_id", professor.departmentID)
                    putInt("course_details_course_id", courseID)
                }
                intent.putExtras(courseDetailsBundle)
                startActivity(intent)
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
                    courseStudentListItemViewAdapter?.filter(query)
                    return false
                }
            })
            searchView?.setQuery(searchQuery, true)

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

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(this, TestRecordsActivity::class.java)
        intent.putExtras(bundle ?: return)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("course_professor_is_configuration_changed",true)
        outState.putString("course_professor_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("course_professor_activity_is_search_query_open",searchView!!.isIconified)
        }
    }

    private fun onRefresh(){
        val databaseHelper = DatabaseHelper.newInstance(this)
        val studentDAO = StudentDAO(databaseHelper)
        if(studentDAO.getNewCurrentStudentsList(professorID, courseID).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_students_string)
            secondTextView.text = getString(R.string.tap_add_button_to_continue_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        courseStudentListItemViewAdapter?.onRefresh()

        if(professorID!=null && courseID!=null){
            val databaseHelper = DatabaseHelper.newInstance(this)
            val professorDAO = ProfessorDAO(databaseHelper)
            val courseDAO = CourseDAO(databaseHelper)
            val professor = professorDAO.get(professorID)
            val course = courseDAO.get(courseID, professor?.departmentID, professor?.collegeID)

            toolBar?.title = getString(R.string.course_students_title, course?.name)
        }
    }
}