package com.example.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.DepartmentListItemViewAdapter
import com.example.ums.bottomsheetdialogs.DepartmentAddFragment
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DepartmentFragment: Fragment(), ItemListener {
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
        val addFloatingButton = view.findViewById<FloatingActionButton>(R.id.add_floating_action_button)
        val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
        val departmentAddFragment = DepartmentAddFragment()
        departmentAddFragment.arguments = Bundle().apply {
            putInt("college_activity_college_id", collegeID!!)
        }
        addFloatingButton.setOnClickListener {
            departmentAddFragment.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
        }

        firstTextView = view.findViewById(R.id.no_departments_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)
        recyclerView.adapter = DepartmentListItemViewAdapter(collegeID!!, departmentDAO, this)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        onRefresh()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("departmentAddFragmentPosition"){_, result->

            val position = result.getInt("position")
            onAdd(position)
        }
    }

    private fun onAdd(position: Int){
        departmentListItemViewAdapter.addItem(position)
        departmentListItemViewAdapter.notifyItemInserted(position)
        onRefresh()
    }

    override fun onUpdate(id: Int) {
    }

    override fun onDelete(id: Int) {
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