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
import com.example.ums.adapters.CollegeAdminListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CollegeAdminAddBottomSheet
import com.example.ums.bottomsheetdialogs.CollegeAdminUpdateBottomSheet
import com.example.ums.dialogFragments.CollegeAdminDeleteDialog
import com.example.ums.interfaces.ItemListener
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.example.ums.superAdminCollegeAdminActivities.CollegeAdminDetailsActivity

class CollegeAdminFragment: ListFragment(), ItemListener {

    private lateinit var collegeAdminDAO: CollegeAdminDAO
    private var collegeAdminListItemViewAdapter: CollegeAdminListItemViewAdapter? = null
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var collegeID: Int? = null
    private var editedCollegeAdminId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("college_activity_college_id")
        collegeAdminDAO = CollegeAdminDAO(DatabaseHelper(requireActivity()))
        if(collegeID!=null){
            collegeAdminListItemViewAdapter = CollegeAdminListItemViewAdapter(collegeID!!, collegeAdminDAO, this )
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
            collegeAdminListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("collegeAdminUpdateFragmentPosition"){_, result->
            val id = result.getInt("id")
            collegeAdminListItemViewAdapter?.updateItemInAdapter(id)
        }
    }
    override fun onUpdate(id: Int) {
        val collegeAdminUpdateBottomSheet = CollegeAdminUpdateBottomSheet.newInstance(id)
        collegeAdminUpdateBottomSheet?.show(requireActivity().supportFragmentManager, "collegeAdminUpdateDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = CollegeAdminDeleteDialog.getInstance(id)
        deleteFragment.show(requireActivity().supportFragmentManager, "collegeAdminDeleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), CollegeAdminDetailsActivity::class.java)
        if(bundle!=null){
            editedCollegeAdminId = bundle.getInt("college_admin_profile_college_admin_id")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onAdd() {
        val collegeAdminAddBottomSheet = CollegeAdminAddBottomSheet.newInstance(collegeID)
        collegeAdminAddBottomSheet?.show(requireActivity().supportFragmentManager, "collegeAdminAddDialog")
    }

    override fun onSearch(query: String?) {
        collegeAdminListItemViewAdapter?.filter(query)
    }

    private fun addAt(id: Int){
        collegeAdminListItemViewAdapter?.addItem(id)
        onRefresh()
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
        if(editedCollegeAdminId!=null){
            collegeAdminListItemViewAdapter?.updateItemInAdapter(editedCollegeAdminId!!)
            editedCollegeAdminId=null
        }
    }
}