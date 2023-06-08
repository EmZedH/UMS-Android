package com.example.ums.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.CollegeActivity
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.CollegeListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CollegeAddBottomSheet
import com.example.ums.bottomsheetdialogs.CollegeUpdateBottomSheet
import com.example.ums.dialogFragments.DeleteDialog
import com.example.ums.listener.AddListener
import com.example.ums.listener.ItemListener
import com.example.ums.listener.SearchListener
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.viewmodels.SuperAdminSharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SuperAdminMainPage : Fragment(), AddListener, SearchListener, ItemListener {

    private lateinit var addCollegeBottomSheet : CollegeAddBottomSheet
    private lateinit var collegeListItemViewAdapter : CollegeListItemViewAdapter
    private lateinit var collegeDAO: CollegeDAO
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private val superAdminSharedViewModel: SuperAdminSharedViewModel by activityViewModels ()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))
        collegeListItemViewAdapter = CollegeListItemViewAdapter(collegeDAO, this)
        superAdminSharedViewModel.setAddListener(this)
        superAdminSharedViewModel.setAdapter(collegeListItemViewAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_college_page, container, false)
        val addFloatingButton = view.findViewById<FloatingActionButton>(R.id.add_floating_action_button)
        val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)

        firstTextView = view.findViewById(R.id.no_departments_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)

        addCollegeBottomSheet = CollegeAddBottomSheet()

        addFloatingButton.setOnClickListener {
            addCollegeBottomSheet.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
        }

        if(collegeDAO.getList().isNotEmpty()){

            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
        recyclerView.adapter = collegeListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }


    override fun onAdd(position: Int) {
        collegeListItemViewAdapter.addItem(position)
        collegeListItemViewAdapter.notifyItemInserted(position)
        onRefresh()
    }

    override fun onSearch(query: String) {
        collegeListItemViewAdapter.filter(query)
    }

    override fun onUpdate(id: Int) {
        val editFragment = CollegeUpdateBottomSheet()
        editFragment.setRotate(true)
        editFragment.arguments = Bundle().apply {
            putInt("college_update_college_id", id)
        }
        editFragment.show((context as AppCompatActivity).supportFragmentManager, "bottomSheetDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = DeleteDialog()
        deleteFragment.setCollegeID(id)
        deleteFragment.show((context as AppCompatActivity).supportFragmentManager, "deleteDialog")
        onRefresh()
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), CollegeActivity::class.java)
        if(bundle!=null){
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun onRefresh(){
        if(collegeDAO.getList().isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }
}

