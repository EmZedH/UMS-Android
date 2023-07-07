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
import com.example.ums.adapters.ProfessorsCoursesListItemViewAdapter
import com.example.ums.dialogFragments.CourseDeleteDialog
import com.example.ums.interfaces.DeleteClickListener
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfessorCoursesListActivity: AppCompatActivity(), DeleteClickListener {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var professorsCoursesListItemViewAdapter: ProfessorsCoursesListItemViewAdapter? = null
    private var professorID: Int? = null
    private var departmentID: Int? = null
    private var collegeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_list_page)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("professor_courses_is_configuration_changed")
            searchQuery = savedInstanceState.getString("professor_courses_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("professor_courses_activity_is_search_query_open")
        }
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bundle = intent.extras
        professorID = bundle?.getInt("professor_profile_professor_id")
        val professorDAO = ProfessorDAO(DatabaseHelper(this))
        val professor = professorDAO.get(professorID)
        collegeID = professor?.collegeID
        departmentID = professor?.departmentID
        if(professor!=null){
            val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(this))
            professorsCoursesListItemViewAdapter = ProfessorsCoursesListItemViewAdapter(professor.user.id, courseProfessorDAO, professorDAO, this)
            val professorID = professor.user.id
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
            recyclerView.adapter = professorsCoursesListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)

            infoButton.setOnClickListener {
                val intent = Intent(this, ProfessorDetailsActivity::class.java)
                val courseDetailsBundle = Bundle().apply {
                    putInt("professor_details_professor_id", professorID)
                }
                intent.putExtras(courseDetailsBundle)
                startActivity(intent)
            }

            addFloatingActionButton.setOnClickListener {

                val intent = Intent(this, CourseSelectForProfessorActivity::class.java)
                val activityBundle = Bundle()
                activityBundle.putInt("course_select_for_professor_activity_department_id", professorID)
                intent.putExtras(activityBundle)
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
                    professorsCoursesListItemViewAdapter?.filter(query)
                    return false
                }
            })
            searchView?.setQuery(searchQuery, true)

            supportFragmentManager.setFragmentResultListener("CourseDeleteDialog", this){_, result->
                val id = result.getInt("courseID")
                professorsCoursesListItemViewAdapter?.deleteItem(id)
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

    override fun onDelete(id: Int) {
        val professorDeleteDialog = CourseDeleteDialog.newInstance(id)
        professorDeleteDialog.show(supportFragmentManager, "CourseDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(this, CourseStudentsListActivity::class.java)
        intent.putExtras(bundle ?: return)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("professor_courses_is_configuration_changed",true)
        outState.putString("professor_courses_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("professor_courses_activity_is_search_query_open",searchView!!.isIconified)
        }
    }

    private fun onRefresh(){
        val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(this))
        if(courseProfessorDAO.getList(professorID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        professorsCoursesListItemViewAdapter?.onRefresh()

        if(professorID!=null){
            val professorDAO = ProfessorDAO(DatabaseHelper(this))
            toolBar?.title = "${professorDAO.get(professorID)?.user?.name} Courses"
        }
    }
}