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
import com.example.ums.ProfessorCoursesListActivity
import com.example.ums.R
import com.example.ums.adapters.ProfessorListItemViewAdapter
import com.example.ums.bottomsheetdialogs.ProfessorAddBottomSheet
import com.example.ums.bottomsheetdialogs.ProfessorUpdateBottomSheet
import com.example.ums.dialogFragments.ProfessorDeleteDialog
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.ProfessorDAO

class ProfessorFragment: AddableSearchableFragment(), ItemListener {

    private lateinit var professorDAO: ProfessorDAO
    private var professorListItemViewAdapter: ProfessorListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var departmentID: Int? = null
    private var collegeID: Int? = null
    private var editProfessorId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        departmentID = arguments?.getInt("department_activity_department_id")
        collegeID = arguments?.getInt("department_activity_college_id")
        professorDAO = ProfessorDAO(DatabaseHelper(requireActivity()))
        if(collegeID!=null){
            professorListItemViewAdapter = ProfessorListItemViewAdapter(departmentID!!, collegeID!!, professorDAO, this )
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
        recyclerView.adapter = professorListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("ProfessorAddFragmentPosition"){_, result->
            val position = result.getInt("id")
            addAt(position)
        }

        setFragmentResultListener("ProfessorDeleteDialog"){_, result->
            val id = result.getInt("id")
            professorListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("ProfessorUpdateFragmentPosition"){_, result->
            val id = result.getInt("id")
            professorListItemViewAdapter?.updateItemInAdapter(id)
        }
    }
    override fun onUpdate(id: Int) {
        val collegeAdminUpdateBottomSheet = ProfessorUpdateBottomSheet()
        collegeAdminUpdateBottomSheet.arguments = Bundle().apply {
            putInt("college_activity_professor_id", id)
        }
        collegeAdminUpdateBottomSheet.show(requireActivity().supportFragmentManager, "collegeAdminUpdateDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = ProfessorDeleteDialog.getInstance(id)
        deleteFragment.show(requireActivity().supportFragmentManager, "ProfessorDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), ProfessorCoursesListActivity::class.java)
        if(bundle!=null){
            editProfessorId = bundle.getInt("professor_profile_professor_id")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onAdd() {
        val professorAddBottomSheet = ProfessorAddBottomSheet()
        professorAddBottomSheet.arguments = Bundle().apply {
            putInt("department_activity_department_id", departmentID!!)
            putInt("department_activity_college_id", collegeID!!)
        }
        professorAddBottomSheet.show(requireActivity().supportFragmentManager, "ProfessorAddDialog")
    }

    override fun onSearch(query: String?) {
        professorListItemViewAdapter?.filter(query)
    }

    private fun addAt(id: Int){
        professorListItemViewAdapter?.addItem(id)
        onRefresh()
    }

    private fun onRefresh(){
        if(professorDAO.getList(departmentID!!, collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_professor_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if(editProfessorId!=null){
            professorListItemViewAdapter?.updateItemInAdapter(editProfessorId!!)
            editProfessorId=null
        }
        else{
            onRefresh()
            professorListItemViewAdapter?.updateList()
        }
    }
}