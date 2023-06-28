package com.example.ums

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.adapters.NewCoursesForProfessorListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CourseAddBottomSheet
import com.example.ums.listener.ClickListener
import com.example.ums.listener.Searchable
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseSelectForProfessorActivity: AppCompatActivity(), ClickListener, Searchable {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var toolBar: MaterialToolbar? = null

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true

    private var newCoursesForProfessorListItemViewAdapter : NewCoursesForProfessorListItemViewAdapter? = null

    private lateinit var courseDAO: CourseDAO

    private var professorID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_select_page)
        val arguments = intent.extras
        professorID = arguments?.getInt("course_select_for_professor_activity_department_id")

        if(savedInstanceState!=null){
            searchQuery = savedInstanceState.getString("course_select_for_professor_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("course_select_for_professor_activity_is_search_query_open")
        }

        courseDAO = CourseDAO(DatabaseHelper(this))
        val professorID = professorID
        if(professorID!=null){
            val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(this))
            val professorDAO = ProfessorDAO(DatabaseHelper(this))
            val professor = professorDAO.get(professorID)
            newCoursesForProfessorListItemViewAdapter = NewCoursesForProfessorListItemViewAdapter(professorID,professorDAO, courseProfessorDAO, courseDAO, this)
            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)

            val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val infoButton = findViewById<ActionMenuItemView>(R.id.info)
            firstTextView.text = getString(R.string.no_courses_string)
            secondTextView.text = getString(R.string.tap_add_button_to_select_courses_string)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = "Select Courses"
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
                val courseAddBottomSheet = CourseAddBottomSheet()
                courseAddBottomSheet.arguments = Bundle().apply {
                    putInt("department_activity_department_id", professor?.departmentID ?: return@setOnClickListener)
                    putInt("department_activity_college_id", professor.collegeID)
                }
                courseAddBottomSheet.show(supportFragmentManager, "CourseAddDialog")
            }

            supportFragmentManager.setFragmentResultListener("CourseAddFragmentPosition", this){_, result->
                val position = result.getInt("id")
                newCoursesForProfessorListItemViewAdapter?.addItem(position)
                onRefresh()
            }

            recyclerView.adapter = newCoursesForProfessorListItemViewAdapter
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
        onRefresh()
    }

    private fun onRefresh(){
        if(courseDAO.getNewCourses(professorID ?: return).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onSearch(query: String?) {
        newCoursesForProfessorListItemViewAdapter?.filter(query)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("course_select_for_professor_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("course_select_for_professor_activity_is_search_query_open",searchView!!.isIconified)
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
}