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
import com.example.ums.adapters.LongClickableListItemViewAdapter
import com.example.ums.bottomsheetdialogs.StudentAddBottomSheet
import com.example.ums.bottomsheetdialogs.StudentUpdateBottomSheet
import com.example.ums.dialogFragments.StudentDeleteDialog
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.model.AdapterItem
import com.example.ums.model.Student
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.superAdminCollegeAdminActivities.StudentActivity

class StudentFragment: ListFragment(), ListIdItemListener {

    private lateinit var studentDAO: StudentDAO
    private var longClickableListItemViewAdapter: LongClickableListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var departmentID: Int? = null
    private var collegeID: Int? = null
    private var editStudentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        departmentID = arguments?.getInt("department_activity_department_id")
        collegeID = arguments?.getInt("department_activity_college_id")
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        studentDAO = StudentDAO(databaseHelper)
        longClickableListItemViewAdapter = LongClickableListItemViewAdapter(getAdapterItems(), this)
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
        recyclerView.adapter = longClickableListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("StudentAddFragmentPosition"){_, result->
            val id = result.getInt("id")
            val student = studentDAO.get(id)
            student?.let {
                longClickableListItemViewAdapter?.addItem(getAdapterItem(it))
            }
            onRefresh()
        }

        setFragmentResultListener("StudentDeleteDialog"){_, result->
            val id = result.getInt("id")
            longClickableListItemViewAdapter?.deleteItem(listOf(id))
            onRefresh()
        }
        setFragmentResultListener("StudentUpdateFragmentPosition"){_, result->
            val id = result.getInt("id")
            val student = studentDAO.get(id)
            student?.let {
                longClickableListItemViewAdapter?.updateItem(getAdapterItem(it))
            }
        }
    }


    override fun onAdd() {
        val professorAddBottomSheet = StudentAddBottomSheet.newInstance(departmentID, collegeID)
        professorAddBottomSheet?.show(requireActivity().supportFragmentManager, "StudentAddDialog")
    }

    override fun onSearch(query: String?) {
        longClickableListItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        if(studentDAO.getList(departmentID, collegeID).isNotEmpty()){
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
        val student = studentDAO.get(editStudentId)
        student?.let {
            longClickableListItemViewAdapter?.updateItem(getAdapterItem(student))
            editStudentId= null
        }
    }
    private fun getAdapterItems(): MutableList<AdapterItem>{
        val list = studentDAO.getList(departmentID, collegeID)
        return list.map {
            getAdapterItem(it)
        }.toMutableList()
    }

    private fun getAdapterItem(student: Student): AdapterItem {
        return AdapterItem(
            listOf(student.user.id),
            "ID : C/${student.collegeID}-D/${student.departmentID}-U/${student.user.id}",
            student.user.name
        )
    }

    override fun onDelete(id: List<Int>) {
        val deleteFragment = StudentDeleteDialog.getInstance(id[0])
        deleteFragment.show(requireActivity().supportFragmentManager, "StudentDeleteDialog")
    }

    override fun onUpdate(id: List<Int>) {
        val collegeAdminUpdateBottomSheet = StudentUpdateBottomSheet.newInstance(id[0])
        collegeAdminUpdateBottomSheet?.show(requireActivity().supportFragmentManager, "StudentUpdateDialog")
    }

    override fun onClick(id: List<Int>) {
        val intent = Intent(requireContext(), StudentActivity::class.java)
        editStudentId = id[0]
        intent.putExtras(Bundle().apply {
            putInt("student_activity_student_id", id[0])
        })
        startActivity(intent)
    }

    override fun onLongClick(id: List<Int>) {
    }
}