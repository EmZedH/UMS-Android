package com.example.ums

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.adapters.CollegePageAdapter
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CollegeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_layout)
        val collegeDAO = CollegeDAO(DatabaseHelper(this))
        val bundle = intent.extras
        val collegeID = bundle?.getInt("collegeID")
        if(collegeID!=null){
            val college = collegeDAO.get(collegeID)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            toolBar.title = college?.name
            toolBar.setNavigationOnClickListener {
                finish()
            }
            val tabAdapter = CollegePageAdapter(this, collegeID)
            val viewPager = findViewById<ViewPager2>(R.id.view_pager)
            val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
            viewPager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, viewPager){tab, position->
                when(position){
                    0->tab.setText(R.string.departments_string)
                    1->tab.setText(R.string.college_admins_string)
                }
            }.attach()
        }
    }
}