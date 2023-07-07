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
import com.example.ums.bottomsheetdialogs.ProfessorAddBottomSheet
import com.example.ums.bottomsheetdialogs.ProfessorUpdateBottomSheet
import com.example.ums.dialogFragments.ProfessorDeleteDialog
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.model.AdapterItem
import com.example.ums.model.Professor
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.example.ums.superAdminCollegeAdminActivities.ProfessorCoursesListActivity

class ProfessorFragment: ListFragment(), ListIdItemListener {

    private lateinit var professorDAO: ProfessorDAO
    private var listItemViewAdapter: ListItemViewAdapter? = null
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
            listItemViewAdapter = ListItemViewAdapter(getAdapterItems(), this)
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
        recyclerView.adapter = listItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("ProfessorAddFragmentPosition"){_, result->
            val id = result.getInt("id")
            val collegeAdmin = professorDAO.get(id)
            collegeAdmin?.let {
                listItemViewAdapter?.addItem(getAdapterItem(it))
            }
            onRefresh()
        }

        setFragmentResultListener("ProfessorDeleteDialog"){_, result->
            val id = result.getInt("id")
            professorDAO.delete(id)
            listItemViewAdapter?.deleteItem(listOf(id))
            onRefresh()
        }
        setFragmentResultListener("ProfessorUpdateFragmentPosition"){_, result->
            val id = result.getInt("id")
            val professor = professorDAO.get(id)
            professor?.let {
                listItemViewAdapter?.updateItem(getAdapterItem(it))
            }
        }
    }
    override fun onAdd() {
        val professorAddBottomSheet = ProfessorAddBottomSheet.newInstance(departmentID, collegeID)
        professorAddBottomSheet?.show(requireActivity().supportFragmentManager, "ProfessorAddDialog")
    }

    override fun onSearch(query: String?) {
        listItemViewAdapter?.filter(query)
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
            val professor = professorDAO.get(editProfessorId)
            professor?.let {
                listItemViewAdapter?.updateItem(getAdapterItem(it))
            }
            editProfessorId=null
        }
        else{
            onRefresh()
            listItemViewAdapter?.updateAdapter(getAdapterItems())
        }
    }
    private fun getAdapterItems(): MutableList<AdapterItem>{
        val list = professorDAO.getList(departmentID, collegeID)
        return list.map {
            getAdapterItem(it)
        }.toMutableList()
    }

    private fun getAdapterItem(professor: Professor): AdapterItem {
        return AdapterItem(
            listOf(professor.user.id),
            "ID : C/${professor.collegeID}-D/${professor.departmentID}-U/${professor.user.id}",
            professor.user.name
        )
    }

    override fun onDelete(id: List<Int>) {
        val deleteFragment = ProfessorDeleteDialog.getInstance(id[0])
        deleteFragment.show(requireActivity().supportFragmentManager, "ProfessorDeleteDialog")
    }

    override fun onUpdate(id: List<Int>) {
        val collegeAdminUpdateBottomSheet = ProfessorUpdateBottomSheet.newInstance(id[0])
        collegeAdminUpdateBottomSheet?.show(requireActivity().supportFragmentManager, "collegeAdminUpdateDialog")
    }

    override fun onClick(id: List<Int>) {
        val intent = Intent(requireContext(), ProfessorCoursesListActivity::class.java)
        editProfessorId = id[0]
        intent.putExtras(
            Bundle().apply {
                putInt("professor_profile_professor_id", id[0])
            }
        )
        startActivity(intent)
    }

    override fun onLongClick(id: List<Int>) {
    }
}