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
import com.example.ums.adapters.CoursesProfessorListItemViewAdapter
import com.example.ums.dialogFragments.ProfessorDeleteDialog
import com.example.ums.interfaces.DeleteClickListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseProfessorsListActivity: AppCompatActivity(), DeleteClickListener {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var courseProfessorListItemViewAdapter: CoursesProfessorListItemViewAdapter? = null
    private var onClickActivity: String? = null
    private var courseID: Int? = null
    private var departmentID: Int? = null
    private var collegeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.professor_list_page)

        if(savedInstanceState!=null){
            onClickActivity = savedInstanceState.getString("on_click_activity")
            isConfigurationChanged = savedInstanceState.getBoolean("course_professor_is_configuration_changed")
            searchQuery = savedInstanceState.getString("course_professor_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("course_professor_activity_is_search_query_open")
        }
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bundle = intent.extras
        collegeID = bundle?.getInt("collegeID")
        courseID = bundle?.getInt("courseID")
        departmentID = bundle?.getInt("departmentID")
        val collegeID = collegeID
        val departmentID = departmentID
        val courseID = courseID
        if(collegeID!=null && courseID!=null && departmentID!=null){
            val databaseHelper = DatabaseHelper.newInstance(this)
            val courseProfessorDAO = CourseProfessorDAO(databaseHelper)
            courseProfessorListItemViewAdapter = CoursesProfessorListItemViewAdapter(courseID, departmentID, collegeID, courseProfessorDAO, this)
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
            recyclerView.adapter = courseProfessorListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)

            infoButton.setOnClickListener {
                val intent = Intent(this, CourseDetailsActivity::class.java)
                val courseDetailsBundle = Bundle().apply {
                    putInt("course_details_college_id", collegeID)
                    putInt("course_details_department_id", departmentID)
                    putInt("course_details_course_id", courseID)
                }
                intent.putExtras(courseDetailsBundle)
                startActivity(intent)
            }

            addFloatingActionButton.setOnClickListener {

                val intent = Intent(this, ProfessorSelectForCourseActivity::class.java)
                val activityBundle = Bundle()
                activityBundle.putInt("course_professor_list_activity_department_id", departmentID)
                activityBundle.putInt("course_professor_list_activity_course_id", courseID)
                activityBundle.putInt("course_professor_list_activity_college_id", collegeID)
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
                    courseProfessorListItemViewAdapter?.filter(query)
                    return false
                }
            })
            searchView?.setQuery(searchQuery, true)

            supportFragmentManager.setFragmentResultListener("ProfessorDeleteDialog", this){_, result->
                val id = result.getInt("id")
                courseProfessorListItemViewAdapter?.deleteItem(id)
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
        val professorDeleteDialog = ProfessorDeleteDialog.getInstance(id)
        professorDeleteDialog.show(supportFragmentManager, "ProfessorDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
//        val intent = Intent(this, StudentClassActivity::class.java)
//        intent.putExtras(bundle ?: return)
//        startActivity(intent)
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
        val courseProfessorDAO = CourseProfessorDAO(databaseHelper)
        if(courseProfessorDAO.getList(courseID, departmentID, collegeID).isNotEmpty()){
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
        courseProfessorListItemViewAdapter?.onRefresh()

        if(courseID!=null && departmentID!=null && collegeID!=null){
            val databaseHelper = DatabaseHelper.newInstance(this)
            val courseDAO = CourseDAO(databaseHelper)
            toolBar?.title = "${courseDAO.get(courseID, departmentID, collegeID)?.name} Professors"
        }
    }
}