package com.example.ums.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ums.fragments.CollegeAdminFragment
import com.example.ums.fragments.DepartmentFragment

class CollegePageAdapter(fragmentActivity: FragmentActivity, private val collegeID: Int): FragmentStateAdapter(fragmentActivity) {
    private val fragments: List<Fragment> = listOf(DepartmentFragment(), CollegeAdminFragment() )

    init {
        for (fragment in fragments){
            fragment.arguments = Bundle().apply {
                putInt("college_activity_college_id", collegeID)
            }
        }
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}