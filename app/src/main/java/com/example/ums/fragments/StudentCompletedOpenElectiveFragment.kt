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
import com.example.ums.StudentCompletedCourseRecordActivity
import com.example.ums.adapters.StudentCompletedOpenCourseListItemViewAdapter
import com.example.ums.dialogFragments.RecordDeleteDialog
import com.example.ums.listener.DeleteClickListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO

class StudentCompletedOpenElectiveFragment: AddableSearchableFragment(), DeleteClickListener {

    private var studentCompletedOpenCourseListItemViewAdapter: StudentCompletedOpenCourseListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        val recordsDAO = RecordsDAO(DatabaseHelper(requireActivity()))
        studentCompletedOpenCourseListItemViewAdapter = StudentCompletedOpenCourseListItemViewAdapter(studentID ?: return, courseDAO, recordsDAO, this )
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
        recyclerView.adapter = studentCompletedOpenCourseListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("RecordDeleteDialog"){_, result->
            val id = result.getInt("position")
            studentCompletedOpenCourseListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
    }

    override fun onDelete(id: Int) {
        val deleteFragment = RecordDeleteDialog.getInstance(id)
        deleteFragment.show(requireActivity().supportFragmentManager, "RecordDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), StudentCompletedCourseRecordActivity::class.java)
        intent.putExtras(bundle ?: return)
        startActivity(intent)
    }

    override fun onSearch(query: String?) {
        studentCompletedOpenCourseListItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        if(courseDAO.getCompletedOpenCourses(studentID ?: return).isNotEmpty()){
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
        studentCompletedOpenCourseListItemViewAdapter?.updateList()
    }

    override fun onAdd() {
    }
}