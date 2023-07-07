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
import com.example.ums.bottomsheetdialogs.DepartmentAddBottomSheet
import com.example.ums.bottomsheetdialogs.DepartmentUpdateBottomSheet
import com.example.ums.dialogFragments.DepartmentDeleteDialog
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.model.AdapterItem
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.example.ums.superAdminCollegeAdminActivities.DepartmentActivity

class DepartmentFragment: ListFragment(), ListIdItemListener {
    private lateinit var departmentDAO: DepartmentDAO

    private var listItemViewAdapter: ListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var collegeID: Int? = null

    private var editCollegeId: Int? = null
    private var editDepartmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("college_activity_college_id")
        departmentDAO = DepartmentDAO(DatabaseHelper(requireActivity()))
        if(collegeID!=null){

            listItemViewAdapter = ListItemViewAdapter(getAdapterItemList(), this)

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
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("departmentAddFragmentPosition"){_, result->
            val id = result.getInt("id")

            val department = departmentDAO.get(id, collegeID)
            department?.let {
                listItemViewAdapter?.addItem(
                    AdapterItem(
                        listOf(it.id, it.collegeID),
                        "ID : C/${it.collegeID}-D/${it.id}",
                        it.name
                    )
                )
            }
            onRefresh()
        }

        setFragmentResultListener("departmentDeleteDialog"){_, result->
            val id = result.getInt("departmentID")
            departmentDAO.delete(id, collegeID)
            collegeID?.let { listItemViewAdapter?.deleteItem(listOf(id, it)) }
            onRefresh()
        }
        setFragmentResultListener("DepartmentUpdateBottomSheet"){_, result->
            val id = result.getInt("departmentID")
            val department = departmentDAO.get(id, collegeID)
            department?.let {
                listItemViewAdapter?.updateItem(
                    AdapterItem(
                        listOf(it.id, it.collegeID),
                        "ID : C/${it.collegeID}-D/${it.id}",
                        it.name
                    )
                )
            }
        }
    }

    override fun onAdd() {
        val departmentAddFragment = DepartmentAddBottomSheet.newInstance(collegeID)
        departmentAddFragment?.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        listItemViewAdapter?.filter(query)
    }

    private fun onRefresh(){
        if(departmentDAO.getList(collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_departments_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        if(editCollegeId!=null && editDepartmentId!=null){
            val department = departmentDAO.get(editDepartmentId, editCollegeId)
            department?.let {
                listItemViewAdapter?.updateItem(
                    AdapterItem(
                        listOf(it.id, it.collegeID),
                        "ID : C/${it.collegeID}-D/${it.id}",
                        it.name
                    )
                )
            }
        }
    }

    override fun onDelete(id: List<Int>) {
        val deleteFragment = DepartmentDeleteDialog.getInstance(id[0])
        deleteFragment.show(requireActivity().supportFragmentManager, "deleteDialog")
    }

    override fun onUpdate(id: List<Int>) {
        val updateBottomSheet = DepartmentUpdateBottomSheet.newInstance(id[0], collegeID)
        updateBottomSheet?.show(requireActivity().supportFragmentManager, "updateDialog")
    }

    override fun onClick(id: List<Int>) {
        val intent = Intent(requireContext(), DepartmentActivity::class.java)
        editCollegeId = id[1]
        editDepartmentId = id[0]
        intent.putExtras(Bundle().apply {
            putInt("collegeID", id[1])
            putInt("departmentID", id[0])
        })
        startActivity(intent)
    }

    override fun onLongClick(id: List<Int>) {
    }

    private fun getAdapterItemList(): MutableList<AdapterItem>{
        val list = departmentDAO.getList(collegeID)
        return list.map {
            AdapterItem(
                listOf(it.id, it.collegeID),
                "ID : C/${it.collegeID}-D/${it.id}",
                it.name
            )
        }.toMutableList()
    }
}