package com.example.ums.fragments

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ums.adapters.DepartmentListItemViewAdapter
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.DepartmentDAO

class CollegeAdminFragment: Fragment(), ItemListener {

    private lateinit var departmentDAO: DepartmentDAO
    private lateinit var departmentListItemViewAdapter: DepartmentListItemViewAdapter
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    override fun onUpdate(id: Int) {
        TODO("Not yet implemented")
    }

    override fun onDelete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun onClick(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}