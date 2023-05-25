package com.example.ums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class SuperAdminMainPage() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dbHelper = DatabaseHelper(requireActivity())
        val collegeDAO = CollegeDAO(dbHelper)
        val collegeList = collegeDAO.getList().toMutableList()

        val view = inflater.inflate(R.layout.fragment_super_admin_main_page, container, false)
        val firstTextView = view.findViewById<TextView>(R.id.no_colleges_text_view)
        val secondTextView = view.findViewById<TextView>(R.id.add_to_get_started_text_view)

        if(collegeList.isNotEmpty()){


            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE

            val recyclerView: RecyclerView = view.findViewById(R.id.college_list_view)
            val adapter = CollegeListItemViewAdapter(collegeList, collegeDAO)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
        else{
//            firstTextView.visibility = View.VISIBLE
//            secondTextView.visibility = View.VISIBLE
        }
        return view
    }
}