package com.example.ums

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.adapters.OtherDepartmentListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CourseAddForStudentBottomSheet
import com.example.ums.bottomsheetdialogs.DepartmentAddBottomSheet
import com.example.ums.listener.Addable
import com.example.ums.listener.ClickListener
import com.example.ums.listener.Searchable
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OtherDepartmentSelectActivity: AppCompatActivity(), Addable, Searchable, ClickListener {
    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private lateinit var departmentDAO: DepartmentDAO
    private var departmentListItemViewAdapter: OtherDepartmentListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var semester: Int? = null
    private var elective: String? = null
    private var degree: String? = null

    private var collegeID: Int? = null
    private var departmentID: Int? = null

    private var editCollegeId: Int? = null
    private var editDepartmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_select_page)
        val arguments = intent.extras
        departmentID = arguments?.getInt("department_id")
        collegeID = arguments?.getInt("college_id")
        semester = arguments?.getInt("student_semester")
        degree = arguments?.getString("student_degree")
        elective = arguments?.getString("student_elective")

        isConfigurationChanged = savedInstanceState?.getBoolean("college_page_is_configuration_changed")
        searchQuery = savedInstanceState?.getString("college_page_activity_search_query")
        isSearchViewOpen = savedInstanceState?.getBoolean("college_page_activity_is_search_query_open") ?: false

        departmentDAO = DepartmentDAO(DatabaseHelper(this))
        if(collegeID!=null && departmentID!=null){
            departmentListItemViewAdapter = OtherDepartmentListItemViewAdapter(departmentID!!, collegeID!!, departmentDAO, this )


            val recyclerView: RecyclerView = findViewById(R.id.list_view)

            firstTextView = findViewById(R.id.no_items_text_view)
            secondTextView = findViewById(R.id.add_to_get_started_text_view)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            searchView = findViewById(R.id.search)
            toolBar?.title = "Select Department"
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
            recyclerView.adapter = departmentListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)

            addFloatingActionButton.setOnClickListener {
                onAdd()
            }
            searchView?.queryHint = getString(R.string.search)
            if(isConfigurationChanged==true){
                searchView?.isIconified = isSearchViewOpen
                isConfigurationChanged = null
            }
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    onSearch(p0)
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    searchQuery = p0
                    onSearch(p0)
                    return false
                }
            })
            searchView?.setQuery(searchQuery, true)

            supportFragmentManager.setFragmentResultListener("departmentAddFragmentPosition", this){_, result->
                val id = result.getInt("id")
                addAt(id)
            }

            supportFragmentManager.setFragmentResultListener("CourseAddForStudentFragmentPosition", this){ _, _ ->
                finish()
            }

            supportFragmentManager.setFragmentResultListener("departmentDeleteDialog", this){_, result->
                val id = result.getInt("departmentID")
                departmentListItemViewAdapter?.deleteItem(id)
                onRefresh()
            }
            supportFragmentManager.setFragmentResultListener("DepartmentUpdateBottomSheet", this){_, result->
                val id = result.getInt("departmentID")
                departmentListItemViewAdapter?.updateItemInAdapter(id)
            }
        }
    }

    override fun onAdd() {
        val departmentAddFragment = DepartmentAddBottomSheet.newInstance(collegeID)
        departmentAddFragment?.show(supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        departmentListItemViewAdapter?.filter(query)
    }

    private fun addAt(id: Int){
        departmentListItemViewAdapter?.addItem(id)
        onRefresh()
    }

//    override fun onUpdate(id: Int) {
//        val updateBottomSheet = DepartmentUpdateBottomSheet.newInstance(id, collegeID)
//
//        updateBottomSheet?.show(supportFragmentManager, "updateDialog")
//    }
//
//    override fun onDelete(id: Int) {
//        val deleteFragment = DepartmentDeleteDialog.getInstance(id)
//        deleteFragment.show(supportFragmentManager, "deleteDialog")
//    }

    override fun onClick(bundle: Bundle?) {
        val courseAddBottomSheet = CourseAddForStudentBottomSheet.newInstance(bundle, degree, semester, "Open")
        courseAddBottomSheet?.show(supportFragmentManager, "CourseAddDialog")
    }

    private fun onRefresh(){
        if(departmentDAO.getOtherDepartment(departmentID!!, collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_departments_string)
            secondTextView.text = getString(R.string.tap_add_button_to_add_department_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        if(editCollegeId!=null && editDepartmentId!=null){
            departmentListItemViewAdapter?.updateItemInAdapter(editCollegeId!!)
            editCollegeId=null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("college_page_is_configuration_changed",true)
        outState.putString("college_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("college_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }
}