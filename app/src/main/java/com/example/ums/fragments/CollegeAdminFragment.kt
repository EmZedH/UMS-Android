package com.example.ums.fragments

import android.os.Bundle
import android.widget.TextView
import com.example.ums.AddableSearchableFragment
import com.example.ums.adapters.DepartmentListItemViewAdapter
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.DepartmentDAO

class CollegeAdminFragment: AddableSearchableFragment(), ItemListener {

    private lateinit var departmentDAO: DepartmentDAO
    private lateinit var departmentListItemViewAdapter: DepartmentListItemViewAdapter
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    override fun onUpdate(id: Int) {
    }

    override fun onDelete(id: Int) {
    }

    override fun onClick(bundle: Bundle?) {
    }

    override fun onAdd() {
    }

    override fun onSearch(query: String?) {
    }
}