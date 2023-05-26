package com.example.ums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.model.databaseAccessObject.CollegeDAO

class SuperAdminMainPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))

        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        val firstTextView = view.findViewById<TextView>(R.id.no_colleges_text_view)
        val secondTextView = view.findViewById<TextView>(R.id.add_to_get_started_text_view)

        if(collegeDAO.getList().isNotEmpty()){

            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE

            val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
            val collegeListItemViewAdapter = CollegeListItemViewAdapter(collegeDAO, this)
            recyclerView.adapter = collegeListItemViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
        return view
    }
}