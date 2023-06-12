package com.example.ums.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.ums.AddableSearchableFragment

class CollegePageAdapter(fragmentActivity: FragmentActivity, private val collegeID: Int, private val fragments: List<AddableSearchableFragment>): FragmentStateAdapter(fragmentActivity) {

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

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        super.onBindViewHolder(holder, position, payloads)

    }
}