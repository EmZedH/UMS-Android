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
import com.example.ums.adapters.CollegeAdminListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CollegeAdminAddBottomSheet
import com.example.ums.dialogFragments.CollegeAdminDeleteDialog
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO

class CollegeAdminFragment: AddableSearchableFragment(), ItemListener {

    private lateinit var departmentDAO: CollegeAdminDAO
    private lateinit var collegeAdminListItemViewAdapter: CollegeAdminListItemViewAdapter
    private lateinit var collegeAdminAddBottomSheet: CollegeAdminAddBottomSheet
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var collegeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("college_activity_college_id")
        departmentDAO = CollegeAdminDAO(DatabaseHelper(requireActivity()))
        if(collegeID!=null){
            collegeAdminListItemViewAdapter = CollegeAdminListItemViewAdapter(collegeID!!, departmentDAO, this )
        }
        collegeAdminAddBottomSheet = CollegeAdminAddBottomSheet()
        collegeAdminAddBottomSheet.arguments = Bundle().apply {
            putInt("college_activity_college_id", collegeID!!)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_college_admin_page, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
        collegeAdminAddBottomSheet = CollegeAdminAddBottomSheet()
        collegeAdminAddBottomSheet.arguments = Bundle().apply {
            putInt("college_activity_college_id", collegeID!!)
        }

        firstTextView = view.findViewById(R.id.no_departments_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)
        onRefresh()
        recyclerView.adapter = collegeAdminListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("collegeAdminAddFragmentPosition"){_, result->
            val position = result.getInt("id")
            addAt(position)
        }

        setFragmentResultListener("collegeAdminDeleteDialog"){_, result->
            val id = result.getInt("id")
            collegeAdminListItemViewAdapter.deleteItem(id)
            onRefresh()
        }
    }
    override fun onUpdate(id: Int) {
    }

    override fun onDelete(id: Int) {

        val deleteFragment = CollegeAdminDeleteDialog.getInstance(id)
        deleteFragment.show((context as AppCompatActivity).supportFragmentManager, "deleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
    }

    override fun onAdd() {
        collegeAdminAddBottomSheet.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        collegeAdminListItemViewAdapter.filter(query)
    }

    private fun addAt(id: Int){

        collegeAdminListItemViewAdapter.addItem(id)
        onRefresh()
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