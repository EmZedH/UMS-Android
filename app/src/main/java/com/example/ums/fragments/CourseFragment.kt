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
import com.example.ums.adapters.ListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CourseAddBottomSheet
import com.example.ums.bottomsheetdialogs.CourseUpdateBottomSheet
import com.example.ums.dialogFragments.CourseDeleteDialog
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.model.AdapterItem
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.superAdminCollegeAdminActivities.CourseProfessorsListActivity

class CourseFragment: ListFragment(), ListIdItemListener {
    private lateinit var courseDAO: CourseDAO

    private var listItemViewAdapter: ListItemViewAdapter? = null

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
        listItemViewAdapter = ListItemViewAdapter(getAdapterItems(), this)
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
        recyclerView.adapter = listItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("CourseAddFragmentPosition"){_, result->
            val id = result.getInt("id")
            val course = courseDAO.get(id, departmentID, collegeID)
            course?.let {
                listItemViewAdapter?.addItem(getAdapterItem(course))
            }
            onRefresh()
        }

        setFragmentResultListener("CourseDeleteDialog"){_, result->
            val id = result.getInt("courseID")
            courseDAO.delete(id, departmentID, collegeID)
            listItemViewAdapter?.deleteItem(listOf(id))
            onRefresh()
        }
        setFragmentResultListener("CourseUpdateBottomSheet"){_, result->
            val id = result.getInt("courseID")
            val course = courseDAO.get(id, departmentID, collegeID)
            course?.let {
                listItemViewAdapter?.updateItem(getAdapterItem(course))
            }
        }
    }
    override fun onAdd() {
        val courseAddBottomSheet = CourseAddBottomSheet.newInstance(departmentID, collegeID)
        courseAddBottomSheet?.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        listItemViewAdapter?.filter(query)
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
            val course = courseDAO.get(editCourseId, departmentID, collegeID)
            course?.let {
                listItemViewAdapter?.updateItem(getAdapterItem(course))
            }
            editCourseId=null
        }
        else{
            onRefresh()
            listItemViewAdapter?.updateAdapter(getAdapterItems())
        }
    }

    override fun onDelete(id: List<Int>) {
        val deleteFragment = CourseDeleteDialog.newInstance(id[0])
        deleteFragment.show(requireActivity().supportFragmentManager, "deleteDialog")
    }

    override fun onUpdate(id: List<Int>) {
        val courseUpdateBottomSheet = CourseUpdateBottomSheet.newInstance(id[0], departmentID, collegeID)
        courseUpdateBottomSheet?.show(requireActivity().supportFragmentManager, "updateDialog")
    }

    override fun onClick(id: List<Int>) {
        val intent = Intent(requireContext(), CourseProfessorsListActivity::class.java)
        editCourseId = id[0]
        val collegeID = collegeID ?: return
        val departmentID = departmentID ?: return
        intent.putExtras(Bundle().apply {
            putInt("courseID", id[0])
            putInt("collegeID", collegeID)
            putInt("departmentID", departmentID)
        })
        startActivity(intent)
    }

    override fun onLongClick(id: List<Int>) {
    }

    private fun getAdapterItems(): MutableList<AdapterItem>{
        val list = courseDAO.getList(departmentID, collegeID)
        return list.map {
            getAdapterItem(it)
        }.toMutableList()
    }

    private fun getAdapterItem(course: Course): AdapterItem {
        return AdapterItem(
            listOf(course.id),
            "ID : C/${course.collegeID}-D/${course.departmentID}-CO/${course.id}",
            course.name
        )
    }
}