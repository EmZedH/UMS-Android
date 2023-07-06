package com.example.ums.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.CourseListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CourseAddBottomSheet
import com.example.ums.bottomsheetdialogs.CourseUpdateBottomSheet
import com.example.ums.dialogFragments.CourseDeleteDialog
import com.example.ums.interfaces.ItemListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.superAdminCollegeAdminActivities.CourseProfessorsListActivity

class CourseFragment: ListFragment(), ItemListener {
    private lateinit var courseDAO: CourseDAO
    private var courseListItemViewAdapter: CourseListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var collegeID: Int? = null
    private var departmentID: Int? = null

    private var editCourseId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        departmentID = arguments?.getInt("department_activity_department_id")
        collegeID = arguments?.getInt("department_activity_college_id")
        courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        courseListItemViewAdapter = CourseListItemViewAdapter(departmentID ?: return, collegeID ?: return, courseDAO, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_page, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_view)

        firstTextView = view.findViewById(R.id.no_items_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)
        onRefresh()
        recyclerView.adapter = courseListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("CourseAddFragmentPosition"){_, result->
            val id = result.getInt("id")
            addAt(id)
        }

        setFragmentResultListener("CourseDeleteDialog"){_, result->
            val id = result.getInt("courseID")
            courseListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("CourseUpdateBottomSheet"){_, result->
            val id = result.getInt("courseID")
            courseListItemViewAdapter?.updateItemInAdapter(id)
        }
    }
    override fun onAdd() {
        val courseAddBottomSheet = CourseAddBottomSheet.newInstance(departmentID, collegeID)
        courseAddBottomSheet?.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        courseListItemViewAdapter?.filter(query)
    }

    override fun onUpdate(id: Int) {
        val courseUpdateBottomSheet = CourseUpdateBottomSheet.newInstance(id, departmentID, collegeID)
        courseUpdateBottomSheet?.show(requireActivity().supportFragmentManager, "updateDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = CourseDeleteDialog.getInstance(id)
        deleteFragment.show(requireActivity().supportFragmentManager, "deleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), CourseProfessorsListActivity::class.java)
        if(bundle!=null){
            editCourseId = bundle.getInt("courseID")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun addAt(id: Int){
        courseListItemViewAdapter?.addItem(id)
        onRefresh()
    }

    private fun onRefresh(){
        if(courseDAO.getList(departmentID!!, collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_courses_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        if(editCourseId!=null){
            courseListItemViewAdapter?.updateItemInAdapter(editCourseId!!)
            editCourseId=null
        }
        else{
            onRefresh()
            courseListItemViewAdapter?.updateList()
        }
    }
}