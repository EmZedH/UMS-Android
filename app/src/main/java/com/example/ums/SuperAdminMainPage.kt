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

        val dbHelper = DatabaseHelper(this.requireActivity())

        val collegeList = CollegeDAO(dbHelper).getList()

        val view = inflater.inflate(R.layout.fragment_super_admin_main_page, container, false)

        if(collegeList.isNotEmpty()){
            val firstTextView = view.findViewById<TextView>(R.id.no_colleges_text_view)
            val secondTextView = view.findViewById<TextView>(R.id.add_to_get_started_text_view)

            firstTextView.text = ""
            secondTextView.text = ""

            val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
            val adapter = CollegeListItemViewAdapter(collegeList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
        return view
    }
}