package com.example.ums

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.adapters.CollegePageAdapter
import com.example.ums.fragments.CollegeAdminFragment
import com.example.ums.fragments.DepartmentFragment
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CollegeActivity: AppCompatActivity() {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var selectedFragment: AddableSearchableFragment? = null
    private var isConfigurationChanged: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("college_page_is_configuration_changed")
            searchQuery = savedInstanceState.getString("college_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("college_page_activity_is_search_query_open")
        }
        setContentView(R.layout.page_layout)
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.add_floating_action_button)
        val collegeDAO = CollegeDAO(DatabaseHelper(this))
        val bundle = intent.extras
        val collegeID = bundle?.getInt("collegeID")
        if(collegeID!=null){
            val college = collegeDAO.get(collegeID)
             val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            toolBar.title = college?.name
            toolBar.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            val fragments: List<AddableSearchableFragment> = listOf(DepartmentFragment(), CollegeAdminFragment() )
            val tabAdapter = CollegePageAdapter(this, collegeID, fragments)
            val viewPager = findViewById<ViewPager2>(R.id.view_pager)
            val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

            viewPager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, viewPager){tab, position->
                when(position){
                    0->tab.setText(R.string.departments_string)
                    1->tab.setText(R.string.college_admins_string)
                }
                viewPager.adapter
            }.attach()
            searchView = findViewById(R.id.search)

            viewPager.registerOnPageChangeCallback(onPageChangeCallback(addFloatingActionButton))
        }
    }

    private fun onPageChangeCallback(addFloatingActionButton: FloatingActionButton): ViewPager2.OnPageChangeCallback{
        return object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    searchView?.setQuery(null, true)
                    selectedFragment = supportFragmentManager.findFragmentByTag("f$position") as AddableSearchableFragment
                    if (selectedFragment is AddableSearchableFragment) {
                        addFloatingActionButton.setOnClickListener {
                            selectedFragment?.onAdd()
                        }
                        searchView?.queryHint = getString(R.string.search)
                        if(isConfigurationChanged==true){
                            searchView?.isIconified = isSearchViewOpen
                            isConfigurationChanged = null
                        }
                        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                            override fun onQueryTextSubmit(p0: String?): Boolean {
                                selectedFragment?.onSearch(p0)
                                return false
                            }

                            override fun onQueryTextChange(p0: String?): Boolean {
                                searchQuery = p0
                                selectedFragment?.onSearch(p0)
                                return false
                            }
                        })
                        searchView?.setQuery(searchQuery, true)
                    }
                    else{
                        addFloatingActionButton.setOnClickListener(null)
                    }
                }
                catch (e: IndexOutOfBoundsException){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(searchView!=null){
            if(!searchView!!.isIconified){
                searchView?.isIconified = true
                return
            }
        }
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("college_page_is_configuration_changed",true)
        outState.putString("college_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("college_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }
}