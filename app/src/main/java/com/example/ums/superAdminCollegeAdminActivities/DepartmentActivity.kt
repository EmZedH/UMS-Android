package com.example.ums.superAdminCollegeAdminActivities

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.DepartmentTabPageAdapter
import com.example.ums.fragments.ListFragment
import com.example.ums.fragments.CourseFragment
import com.example.ums.fragments.ProfessorFragment
import com.example.ums.fragments.StudentFragment
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DepartmentActivity: AppCompatActivity() {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private var departmentID: Int? = null
    private var collegeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbed_page_with_info_button_layout)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("department_page_is_configuration_changed")
            searchQuery = savedInstanceState.getString("department_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("department_page_activity_is_search_query_open")
            departmentID = savedInstanceState.getInt("department_page_activity_college_id")
        }
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val departmentDAO = DepartmentDAO(DatabaseHelper(this))
        val bundle = intent.extras
        departmentID = bundle?.getInt("departmentID")
        collegeID = bundle?.getInt("collegeID")

        val collegeID = collegeID
        val departmentID = departmentID
        if(collegeID!=null && departmentID!=null){
            val department = departmentDAO.get(departmentID, collegeID)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = department?.name
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            val fragments: List<ListFragment> = listOf(CourseFragment(), ProfessorFragment(), StudentFragment())
            val tabAdapter = DepartmentTabPageAdapter(this, collegeID, departmentID, fragments)
            val viewPager = findViewById<ViewPager2>(R.id.view_pager)
            val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

            viewPager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, viewPager){tab, position->
                when(position){
                    0-> tab.text = "Courses"
                    1-> tab.text = "Professors"
                    2-> tab.text = "Students"
                }
                viewPager.adapter
            }.attach()
            searchView = findViewById(R.id.search)
            val infoButton = findViewById<ActionMenuItemView>(R.id.info)

            infoButton.setOnClickListener {
                val intent = Intent(this, DepartmentDetailsActivity::class.java)
                val departmentDetailsBundle = Bundle().apply {
                    putInt("department_details_department_id", departmentID)
                    putInt("department_details_college_id", collegeID)
                }
                intent.putExtras(departmentDetailsBundle)
                startActivity(intent)
            }

            viewPager.currentItem
            viewPager.registerOnPageChangeCallback(onPageChangeCallback(addFloatingActionButton, viewPager, tabAdapter))

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })
        }
    }

    private fun onPageChangeCallback(addFloatingActionButton: FloatingActionButton, viewPager: ViewPager2, adapter: DepartmentTabPageAdapter): ViewPager2.OnPageChangeCallback{
        return object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    searchView?.setQuery(null, true)
                    var addableSearchableFragment = supportFragmentManager.findFragmentByTag("f$position")
                    if(addableSearchableFragment == null){
                        addableSearchableFragment = adapter.createFragment(viewPager.currentItem)
                    }
                    val selectedFragment = addableSearchableFragment
                    if (selectedFragment is ListFragment) {
                        addFloatingActionButton.setOnClickListener {
                            selectedFragment.onAdd()
                        }
                        searchView?.queryHint = getString(R.string.search)
                        if(isConfigurationChanged==true){
                            searchView?.isIconified = isSearchViewOpen
                            isConfigurationChanged = null
                        }
                        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                            override fun onQueryTextSubmit(p0: String?): Boolean {
                                selectedFragment.onSearch(p0)
                                return false
                            }

                            override fun onQueryTextChange(p0: String?): Boolean {
                                searchQuery = p0
                                selectedFragment.onSearch(p0)
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
                catch (e: NullPointerException){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun onBack(){
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
        outState.putBoolean("department_page_is_configuration_changed",true)
        outState.putString("department_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("department_page_activity_is_search_query_open",searchView!!.isIconified)
        }
        if(departmentID!=null){
            outState.putInt("department_page_activity_college_id", departmentID!!)
        }
    }

    override fun onResume() {
        super.onResume()

        if(departmentID!=null && collegeID!=null){
            toolBar?.title = DepartmentDAO(DatabaseHelper(this)).get(departmentID!!, collegeID!!)?.name
        }
    }
}