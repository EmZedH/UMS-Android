package com.example.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.AddableSearchableFragment
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.DepartmentListItemViewAdapter
import com.example.ums.bottomsheetdialogs.DepartmentAddBottomSheet
import com.example.ums.bottomsheetdialogs.DepartmentUpdateBottomSheet
import com.example.ums.dialogFragments.DepartmentDeleteDialog
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.DepartmentDAO

class DepartmentFragment: AddableSearchableFragment(), ItemListener {
    private lateinit var departmentAddFragment: DepartmentAddBottomSheet
    private lateinit var departmentDAO: DepartmentDAO
    private lateinit var departmentListItemViewAdapter: DepartmentListItemViewAdapter
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var collegeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("college_activity_college_id")
        departmentDAO = DepartmentDAO(DatabaseHelper(requireActivity()))
        if(collegeID!=null){
            departmentListItemViewAdapter = DepartmentListItemViewAdapter(collegeID!!, departmentDAO, this )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_department_page, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
        departmentAddFragment = DepartmentAddBottomSheet()
        departmentAddFragment.arguments = Bundle().apply {
            putInt("college_activity_college_id", collegeID!!)
        }

        firstTextView = view.findViewById(R.id.no_departments_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)
        onRefresh()
        recyclerView.adapter = departmentListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("departmentAddFragmentPosition"){_, result->

            val position = result.getInt("position")
            addAt(position)
        }

        setFragmentResultListener("departmentDeleteDialog"){_, result->
            val id = result.getInt("departmentID")
            departmentListItemViewAdapter.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("DepartmentUpdateBottomSheet"){_, result->
            val id = result.getInt("departmentID")
            departmentListItemViewAdapter.updateItemInAdapter(departmentDAO.getList(collegeID!!).indexOf(departmentDAO.get(id, collegeID!!)))
        }
    }

    override fun onAdd() {
        departmentAddFragment.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        departmentListItemViewAdapter.filter(query)
    }

    private fun addAt(position: Int){
        departmentListItemViewAdapter.addItem(position)
        departmentListItemViewAdapter.notifyItemInserted(position)
        onRefresh()
    }

    override fun onUpdate(id: Int) {
        val updateBottomSheet = DepartmentUpdateBottomSheet()

        updateBottomSheet.arguments = Bundle().apply{
            putInt("department_update_college_id", collegeID!!)
            putInt("department_update_department_id", id)
        }

        updateBottomSheet.show((context as AppCompatActivity).supportFragmentManager, "updateDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = DepartmentDeleteDialog.getInstance(id)
        deleteFragment.show((context as AppCompatActivity).supportFragmentManager, "deleteDialog")
    }

    override fun onClick(bundle: Bundle?) {

    }

    private fun onRefresh(){
        if(departmentDAO.getList(collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
}