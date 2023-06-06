package com.example.ums

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
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        firstTextView = view.findViewById(R.id.no_colleges_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)

        addCollegeBottomSheet = CollegeAddBottomSheet()

        val addFloatingButton = view.findViewById<FloatingActionButton>(R.id.add_floating_action_button)

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
        val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
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
        superAdminSharedViewModel.setID(id)
//        superAdminSharedViewModel.setAdapter(collegeListItemViewAdapter)
        val editFragment = CollegeUpdateBottomSheet()
        editFragment.setRotate(true)
        editFragment.show((context as AppCompatActivity).supportFragmentManager, "bottomSheetDialog")
    }

    override fun onDelete(id: Int) {
//        superAdminSharedViewModel.setID(id)
//        superAdminSharedViewModel.setAdapter(collegeListItemViewAdapter)
        val deleteFragment = DeleteDialog()
        deleteFragment.setCollegeID(id)
        deleteFragment.show((context as AppCompatActivity).supportFragmentManager, "deleteDialog")
        onRefresh()
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

