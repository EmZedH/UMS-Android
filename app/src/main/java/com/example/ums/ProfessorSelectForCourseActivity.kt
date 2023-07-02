package com.example.ums

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.adapters.NewProfessorsForCoursesListItemViewAdapter
import com.example.ums.bottomsheetdialogs.ProfessorAddBottomSheet
import com.example.ums.listener.ClickListener
import com.example.ums.listener.Searchable
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfessorSelectForCourseActivity: AppCompatActivity(), ClickListener, Searchable {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var toolBar: MaterialToolbar? = null

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true

    private var newProfessorsForCoursesListItemViewAdapter : NewProfessorsForCoursesListItemViewAdapter? = null
    private lateinit var professorDAO: ProfessorDAO

    private var collegeID: Int? = null
    private var departmentID: Int? = null
    private var courseID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_select_page)
        val arguments = intent.extras
        departmentID = arguments?.getInt("course_professor_list_activity_department_id")
        collegeID = arguments?.getInt("course_professor_list_activity_college_id")
        courseID = arguments?.getInt("course_professor_list_activity_course_id")

        if(savedInstanceState!=null){
            searchQuery = savedInstanceState.getString("course_professor_list_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("course_professor_list_activity_is_search_query_open")
        }

        professorDAO = ProfessorDAO(DatabaseHelper(this))
        if(courseID!=null && departmentID !=null && collegeID!=null){
            val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(this))
            val courseDAO = CourseDAO(DatabaseHelper(this))
            newProfessorsForCoursesListItemViewAdapter = NewProfessorsForCoursesListItemViewAdapter(courseID!!, departmentID!!, collegeID!!, professorDAO, courseProfessorDAO, courseDAO, this)
            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)

            val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)

            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = "Add Professor to Course"
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
                val professorAddBottomSheet = ProfessorAddBottomSheet.newInstance(departmentID, collegeID)
                professorAddBottomSheet?.show(supportFragmentManager, "ProfessorAddDialog")
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
        onRefresh()
    }

    private fun onRefresh(){
        val courseID = courseID
        val departmentID = departmentID
        val collegeID = collegeID

        if(courseID!=null && departmentID!=null && collegeID!=null && professorDAO.getNewProfessors(courseID, departmentID, collegeID).isNotEmpty()){
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
}