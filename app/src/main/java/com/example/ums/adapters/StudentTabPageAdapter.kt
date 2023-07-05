package com.example.ums.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ums.fragments.ListFragment

class StudentTabPageAdapter(fragmentActivity: FragmentActivity, private val studentID: Int, private val fragments: List<ListFragment>): FragmentStateAdapter(fragmentActivity) {

    init {
        for (fragment in fragments){
            fragment.arguments = Bundle().apply {
                putInt("student_activity_student_id", studentID)
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