package com.example.ums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.bottomsheetdialogs.AddCollegeBottomSheet
import com.example.ums.bottomsheetdialogs.FragmentRefreshListener
import com.example.ums.listener.AddCollegeListener
import com.example.ums.listener.SearchListener
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SuperAdminMainPage : Fragment(), AddCollegeListener, SearchListener, FragmentRefreshListener {

    private lateinit var addCollegeBottomSheet : AddCollegeBottomSheet
    private lateinit var collegeListItemViewAdapter : CollegeListItemViewAdapter
    private lateinit var collegeDAO: CollegeDAO
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))
        collegeListItemViewAdapter = CollegeListItemViewAdapter(collegeDAO, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        firstTextView = view.findViewById(R.id.no_colleges_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)

        addCollegeBottomSheet = AddCollegeBottomSheet(collegeDAO, this)

        val addFloatingButton = view.findViewById<FloatingActionButton>(R.id.add_floating_action_button)

        addFloatingButton.setOnClickListener {
            addCollegeBottomSheet.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
        }

        if(collegeDAO.getList().isNotEmpty()){

            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE

            val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
            recyclerView.adapter = collegeListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
        else{

            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
        return view
    }

    override fun addItemToAdapter(position: Int) {
        collegeListItemViewAdapter.addItem(position)
        collegeListItemViewAdapter.notifyItemInserted(position)
        onRefresh()
    }

    override fun onSearch(query: String) {
        collegeListItemViewAdapter.filter(query)
    }

    override fun onRefresh() {

        if(collegeDAO.getList().isNotEmpty()){

            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE

            val recyclerView: RecyclerView? = view?.findViewById(R.id.college_list_view)
            recyclerView?.adapter = collegeListItemViewAdapter
            recyclerView?.layoutManager = LinearLayoutManager(this.context)
        }
        else{

            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

}