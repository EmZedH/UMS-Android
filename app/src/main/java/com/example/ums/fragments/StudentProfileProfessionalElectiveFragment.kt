package com.example.ums.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.SearchableFragment
import com.example.ums.adapters.StudentProfileProfessionalCourseListItemViewAdapter
import com.example.ums.listener.DeleteClickListener
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.studentActivities.StudentTestActivity

class StudentProfileProfessionalElectiveFragment: SearchableFragment(), DeleteClickListener {

    private var studentProfessionalCourseListItemViewAdapter: StudentProfileProfessionalCourseListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var studentID: Int? = null
    private var activityString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        activityString = arguments?.getString("activity_name")
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        studentProfessionalCourseListItemViewAdapter = StudentProfileProfessionalCourseListItemViewAdapter(
            studentID ?: return,
            courseDAO,
            this
        )
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

    override fun onDelete(id: Int) {
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), StudentTestActivity::class.java)
        if(bundle!=null){
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

//    override fun onAdd() {
//    }

    override fun onSearch(query: String?) {
        studentProfessionalCourseListItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        if(courseDAO.getProfessionalCourses(studentID ?: return).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_courses_string)
            secondTextView.text = getString(R.string.pay_fees_to_register_courses_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        studentProfessionalCourseListItemViewAdapter?.updateList()
    }
}