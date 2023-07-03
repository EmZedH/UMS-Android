package com.example.ums.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.SearchableFragment
import com.example.ums.adapters.StudentProfileOpenCourseListItemViewAdapter
import com.example.ums.listener.DeleteClickListener
import com.example.ums.model.Records
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.studentActivities.StudentTestActivity

class StudentProfileOpenElectiveFragment: SearchableFragment(), DeleteClickListener {

    private var studentOpenCourseListItemViewAdapter: StudentProfileOpenCourseListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var studentID: Int? = null
    private var activityString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        activityString = arguments?.getString("activity_name")
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        studentOpenCourseListItemViewAdapter = StudentProfileOpenCourseListItemViewAdapter(
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

        recyclerView.adapter = studentOpenCourseListItemViewAdapter
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

    override fun onSearch(query: String?) {
        studentOpenCourseListItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        if(courseDAO.getOpenCourses(studentID ?: return).isNotEmpty()){
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
        studentOpenCourseListItemViewAdapter?.updateList()
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val courseID = data?.getIntExtra("course_id", -1)
            val departmentID = data?.getIntExtra("department_id", -1)
            val collegeID = data?.getIntExtra("college_id", -1)
            val professorID = data?.getIntExtra("professor_id", -1)
            val transactionID = data?.getIntExtra("transaction_id", -1)
            val recordsDAO = RecordsDAO(DatabaseHelper(requireActivity()))
            val courseProfessorDAO = CourseProfessorDAO(DatabaseHelper(requireActivity()))
            val courseProfessor = courseProfessorDAO.get(professorID, courseID, departmentID, collegeID)

            if(courseID!=null && courseID != -1 && departmentID != null &&
                departmentID != -1 && collegeID != null && collegeID != -1 &&
                professorID!=null && professorID!=-1 &&
                transactionID!=null && transactionID!=-1 && courseProfessor!=null){
                recordsDAO.insert(
                    Records(
                        studentID ?: return@registerForActivityResult,
                        courseProfessor,
                        transactionID,
                        0,
                        0,
                        0,
                        "NOT_COMPLETED",
                        0
                    )
                )
                onRefresh()
                studentOpenCourseListItemViewAdapter?.updateList()
            }

        }
    }
}