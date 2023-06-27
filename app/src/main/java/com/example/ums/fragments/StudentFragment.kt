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
import com.example.ums.AddableSearchableFragment
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.StudentActivity
import com.example.ums.adapters.StudentListItemViewAdapter
import com.example.ums.bottomsheetdialogs.StudentAddBottomSheet
import com.example.ums.bottomsheetdialogs.StudentUpdateBottomSheet
import com.example.ums.dialogFragments.StudentDeleteDialog
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.StudentDAO

class StudentFragment: AddableSearchableFragment(), ItemListener {

    private lateinit var studentDAO: StudentDAO
    private var studentListItemViewAdapter: StudentListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var departmentID: Int? = null
    private var collegeID: Int? = null
    private var editStudentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        departmentID = arguments?.getInt("department_activity_department_id")
        collegeID = arguments?.getInt("department_activity_college_id")
        studentDAO = StudentDAO(DatabaseHelper(requireActivity()))
        if(collegeID!=null){
            studentListItemViewAdapter = StudentListItemViewAdapter(departmentID!!, collegeID!!, studentDAO, this )
        }
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
        recyclerView.adapter = studentListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("StudentAddFragmentPosition"){_, result->
            val position = result.getInt("id")
            addAt(position)
        }

        setFragmentResultListener("StudentDeleteDialog"){_, result->
            val id = result.getInt("id")
            studentListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("StudentUpdateFragmentPosition"){_, result->
            val id = result.getInt("id")
            studentListItemViewAdapter?.updateItemInAdapter(id)
        }
    }
    override fun onUpdate(id: Int) {
        val collegeAdminUpdateBottomSheet = StudentUpdateBottomSheet()
        collegeAdminUpdateBottomSheet.arguments = Bundle().apply {
            putInt("department_activity_student_id", id)
        }
        collegeAdminUpdateBottomSheet.show(requireActivity().supportFragmentManager, "StudentUpdateDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = StudentDeleteDialog.getInstance(id)
        deleteFragment.show(requireActivity().supportFragmentManager, "StudentDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), StudentActivity::class.java)
        if(bundle!=null){
            editStudentId = bundle.getInt("student_activity_student_id")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onAdd() {
        val professorAddBottomSheet = StudentAddBottomSheet()
        professorAddBottomSheet.arguments = Bundle().apply {
            putInt("department_activity_department_id", departmentID!!)
            putInt("department_activity_college_id", collegeID!!)
        }
        professorAddBottomSheet.show(requireActivity().supportFragmentManager, "StudentAddDialog")
    }

    override fun onSearch(query: String?) {
        studentListItemViewAdapter?.filter(query)
    }

    private fun addAt(id: Int){
        studentListItemViewAdapter?.addItem(id)
        onRefresh()
    }

    private fun onRefresh(){
        if(studentDAO.getList(departmentID!!, collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_students_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if(editStudentId!=null){
            studentListItemViewAdapter?.updateItemInAdapter(editStudentId!!)
            editStudentId=null
        }
    }
}