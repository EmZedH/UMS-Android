package com.example.ums.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.StudentTransactionSelectActivity
import com.example.ums.TestRecordsActivity
import com.example.ums.adapters.StudentProfessionalCourseListItemViewAdapter
import com.example.ums.dialogFragments.RecordDeleteDialog
import com.example.ums.listener.DeleteClickListener
import com.example.ums.model.Records
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO

class StudentProfessionalElectiveFragment: AddableSearchableFragment(), DeleteClickListener {

    companion object{
        const val FRAGMENT_KEY = "StudentProfessionalElectiveFragment"
    }

    private var studentProfessionalCourseListItemViewAdapter: StudentProfessionalCourseListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var studentID: Int? = null
    private var activityString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        activityString = arguments?.getString("activity_name")
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        val recordsDAO = RecordsDAO(DatabaseHelper(requireActivity()))
        val departmentID = StudentDAO(DatabaseHelper(requireActivity())).get(studentID)?.departmentID
        studentProfessionalCourseListItemViewAdapter = StudentProfessionalCourseListItemViewAdapter(studentID ?: return,departmentID ?: return, courseDAO, recordsDAO, this )
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
            val position = result.getInt("course_id")
            studentProfessionalCourseListItemViewAdapter?.deleteItem(position)
            onRefresh()
        }
    }

    override fun onDelete(id: Int) {
        val deleteFragment = RecordDeleteDialog.getInstance(id, FRAGMENT_KEY)
        deleteFragment.show(requireActivity().supportFragmentManager, "RecordDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), TestRecordsActivity::class.java)
        if(bundle!=null){
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onAdd() {
        val intent = Intent(requireContext(), StudentTransactionSelectActivity::class.java)
        val bundle = Bundle().apply {
            putString("student_transaction_select_activity_activity_name","StudentProfessionalCourseSelectActivity")
            putInt("student_transaction_select_activity_student_id", studentID ?: return)
        }
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

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
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        studentProfessionalCourseListItemViewAdapter?.updateList()
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
                studentProfessionalCourseListItemViewAdapter?.updateList()
            }

        }
    }
}