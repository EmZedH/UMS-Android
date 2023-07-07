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
import com.example.ums.bottomsheetdialogs.CollegeAdminAddBottomSheet
import com.example.ums.bottomsheetdialogs.CollegeAdminUpdateBottomSheet
import com.example.ums.dialogFragments.CollegeAdminDeleteDialog
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.model.AdapterItem
import com.example.ums.model.CollegeAdmin
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.example.ums.superAdminCollegeAdminActivities.CollegeAdminDetailsActivity

class CollegeAdminFragment: ListFragment(), ListIdItemListener {

    private lateinit var collegeAdminDAO: CollegeAdminDAO

    private var listItemViewAdapter: ListItemViewAdapter? = null

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var collegeID: Int? = null
    private var editedCollegeAdminId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("college_activity_college_id")
        collegeAdminDAO = CollegeAdminDAO(DatabaseHelper(requireActivity()))
        listItemViewAdapter = ListItemViewAdapter(getAdapterItems(), this)
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

        setFragmentResultListener("collegeAdminAddFragmentPosition"){_, result->
            val id = result.getInt("id")
            val collegeAdmin = collegeAdminDAO.get(id)
            collegeAdmin?.let {
                listItemViewAdapter?.addItem(getAdapterItem(it))
            }
            onRefresh()
        }

        setFragmentResultListener("collegeAdminDeleteDialog"){_, result->
            val id = result.getInt("id")
            collegeAdminDAO.delete(id)
            listItemViewAdapter?.deleteItem(listOf(id))
            onRefresh()
        }
        setFragmentResultListener("collegeAdminUpdateFragmentPosition"){_, result->
            val id = result.getInt("id")
            val collegeAdmin = collegeAdminDAO.get(id)
            collegeAdmin?.let {
                listItemViewAdapter?.updateItem(getAdapterItem(it))
            }
        }
    }

    override fun onAdd() {
        val collegeAdminAddBottomSheet = CollegeAdminAddBottomSheet.newInstance(collegeID)
        collegeAdminAddBottomSheet?.show(requireActivity().supportFragmentManager, "collegeAdminAddDialog")
    }

    override fun onSearch(query: String?) {
        listItemViewAdapter?.filter(query)
    }
    private fun onRefresh(){
        if(collegeAdminDAO.getList(collegeID!!).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.text = getString(R.string.no_college_admin_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        val collegeAdmin = collegeAdminDAO.get(editedCollegeAdminId)
        collegeAdmin?.let {
            listItemViewAdapter?.updateItem(
                getAdapterItem(it)
            )
        }
    }
    override fun onDelete(id: List<Int>) {
        val deleteFragment = CollegeAdminDeleteDialog.getInstance(id[0])
        deleteFragment.show(requireActivity().supportFragmentManager, "collegeAdminDeleteDialog")
    }

    override fun onUpdate(id: List<Int>) {
        val collegeAdminUpdateBottomSheet = CollegeAdminUpdateBottomSheet.newInstance(id[0])
        collegeAdminUpdateBottomSheet?.show(requireActivity().supportFragmentManager, "collegeAdminUpdateDialog")
    }

    override fun onClick(id: List<Int>) {
        val intent = Intent(requireContext(), CollegeAdminDetailsActivity::class.java)
        editedCollegeAdminId = id[0]
        intent.putExtras(Bundle().apply {
            putInt("college_admin_profile_college_admin_id", id[0])
            putInt("college_admin_profile_college_id", collegeID ?: return)
        })
        startActivity(intent)
    }

    override fun onLongClick(id: List<Int>) {
    }
    private fun getAdapterItems(): MutableList<AdapterItem>{
        val list = collegeAdminDAO.getList(collegeID)
        return list.map {
            getAdapterItem(it)
        }.toMutableList()
    }

    private fun getAdapterItem(collegeAdmin: CollegeAdmin): AdapterItem{
        return AdapterItem(
            listOf(collegeAdmin.user.id),
            "ID : C/${collegeAdmin.collegeID}-U/${collegeAdmin.user.id}",
            collegeAdmin.user.name
        )
    }
}