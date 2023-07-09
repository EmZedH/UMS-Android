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
import com.example.ums.CompletionStatus
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.StudentOpenCourseListItemViewAdapter
import com.example.ums.dialogFragments.RecordDeleteDialog
import com.example.ums.dialogFragments.StudentOpenCourseAddConfirmationDialog
import com.example.ums.interfaces.OpenCourseItemListener
import com.example.ums.model.Records
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.superAdminCollegeAdminActivities.StudentTransactionSelectActivity
import com.example.ums.superAdminCollegeAdminActivities.TestRecordsActivity

class StudentOpenElectiveFragment: ListFragment(), OpenCourseItemListener {

    companion object{
        const val FRAGMENT_KEY = "StudentOpenElectiveFragment"
    }

    private var studentOpenCourseListItemViewAdapter: StudentOpenCourseListItemViewAdapter? = null
    private var firstTextView: TextView? = null
    private var secondTextView: TextView? = null

    private var studentID: Int? = null
    private var activityString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_activity_student_id")
        activityString = arguments?.getString("activity_name")
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val courseDAO = CourseDAO(databaseHelper)
        val recordsDAO = RecordsDAO(databaseHelper)
        studentOpenCourseListItemViewAdapter = StudentOpenCourseListItemViewAdapter(studentID ?: return, courseDAO, recordsDAO, this )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("RecordDeleteDialog$FRAGMENT_KEY"){ _, result->
            val id = result.getInt("course_id")
            val departmentID = result.getInt("department_id")
            studentOpenCourseListItemViewAdapter?.deleteItem(id, departmentID)
            onRefresh()
        }

        setFragmentResultListener("StudentOpenCourseAddConfirmationDialog"){_, _->
            showAddCourseActivity()
        }
    }

    override fun onDelete(courseID: Int, departmentID: Int) {
        val deleteFragment = RecordDeleteDialog.getInstance(courseID, departmentID, FRAGMENT_KEY)
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
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val courseDAO = CourseDAO(databaseHelper)
        if(courseDAO.getOpenCourses(studentID).size >= 2){
            val studentOpenCourseAddConfirmationDialog = StudentOpenCourseAddConfirmationDialog()
            studentOpenCourseAddConfirmationDialog.show(requireActivity().supportFragmentManager, "StudentOpenCourseAddConfirmationDialog")
            return
        }
        showAddCourseActivity()
    }

    private fun showAddCourseActivity(){
        val intent = Intent(requireContext(), StudentTransactionSelectActivity::class.java)
        val bundle = Bundle().apply {
            putString("student_transaction_select_activity_activity_name", "StudentOpenCourseSelectActivity")
            putInt("student_transaction_select_activity_student_id", studentID ?: return)
        }
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    override fun onSearch(query: String?) {
        studentOpenCourseListItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val courseDAO = CourseDAO(databaseHelper)
        if(courseDAO.getOpenCourses(studentID).isNotEmpty()){
            firstTextView?.visibility = View.INVISIBLE
            secondTextView?.visibility = View.INVISIBLE
        }
        else{
            firstTextView?.text = getString(R.string.no_courses_string)
            firstTextView?.visibility = View.VISIBLE
            secondTextView?.visibility = View.VISIBLE
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
            val databaseHelper = DatabaseHelper.newInstance(requireContext())
            val recordsDAO = RecordsDAO(databaseHelper)
            val courseProfessorDAO = CourseProfessorDAO(databaseHelper)
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
                            CompletionStatus.NOT_COMPLETED.status,
                            0
                        )
                    )
                onRefresh()
                studentOpenCourseListItemViewAdapter?.updateList()
            }

        }
    }
}