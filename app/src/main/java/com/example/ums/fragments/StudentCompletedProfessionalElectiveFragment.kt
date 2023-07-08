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
import com.example.ums.adapters.StudentCompletedProfessionalListItemViewAdapter
import com.example.ums.dialogFragments.RecordDeleteDialog
import com.example.ums.interfaces.DeleteClickListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.superAdminCollegeAdminActivities.StudentCompletedCourseRecordActivity

class StudentCompletedProfessionalElectiveFragment: ListFragment(), DeleteClickListener {

    companion object{
        const val FRAGMENT_KEY = "StudentCompletedProfessionalElectiveFragment"
    }

    private var studentProfessionalCourseListItemViewAdapter: StudentCompletedProfessionalListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val courseDAO = CourseDAO(databaseHelper)
        val recordsDAO = RecordsDAO(databaseHelper)
        val departmentID = StudentDAO(databaseHelper).get(studentID)?.departmentID
        studentProfessionalCourseListItemViewAdapter = StudentCompletedProfessionalListItemViewAdapter(studentID ?: return,departmentID ?: return, courseDAO, recordsDAO, this )
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
        recyclerView.adapter = studentProfessionalCourseListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("RecordDeleteDialog$FRAGMENT_KEY"){ _, result->
            val position = result.getInt("position")
            studentProfessionalCourseListItemViewAdapter?.deleteItem(position)
            onRefresh()
        }
    }

    override fun onDelete(id: Int) {
        val deleteFragment = RecordDeleteDialog.getInstance(id, FRAGMENT_KEY)
        deleteFragment.show(requireActivity().supportFragmentManager, "RecordDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), StudentCompletedCourseRecordActivity::class.java)
        intent.putExtras(bundle ?: return)
        startActivity(intent)
    }

    override fun onSearch(query: String?) {
        studentProfessionalCourseListItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val courseDAO = CourseDAO(databaseHelper)
        if(courseDAO.getCompletedProfessionalCourses(studentID ?: return).isNotEmpty()){
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
        onRefresh()
        studentProfessionalCourseListItemViewAdapter?.updateList()
    }

    override fun onAdd() {
    }

}