package com.example.ums.superAdminCollegeAdminActivities

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.StudentCompletedTabPageAdapter
import com.example.ums.fragments.ListFragment
import com.example.ums.fragments.StudentCompletedOpenElectiveFragment
import com.example.ums.fragments.StudentCompletedProfessionalElectiveFragment
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StudentCompletedCourseActivity: AppCompatActivity() {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private var studentID: Int? = null
    private var button: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbed_page_layout_without_fab)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("student_completed_course_page_is_configuration_changed")
            searchQuery = savedInstanceState.getString("college_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("college_page_activity_is_search_query_open")
        }
        val studentDAO = StudentDAO(DatabaseHelper(this))
        val bundle = intent.extras
        studentID = bundle?.getInt("student_id")
        val studentID = studentID
        if(studentID!=null){
            val student = studentDAO.get(studentID)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = "${ student?.user?.name } Records"
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            val fragments: List<ListFragment> = listOf(StudentCompletedProfessionalElectiveFragment(), StudentCompletedOpenElectiveFragment() )
            val tabAdapter = StudentCompletedTabPageAdapter(this, studentID, fragments)
            val viewPager = findViewById<ViewPager2>(R.id.view_pager)
            val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

            viewPager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, viewPager){tab, position->
                when(position){
                    0->tab.text = "Professional Courses"
                    1->tab.text = "Open Courses"
                }
                viewPager.adapter
            }.attach()
            searchView = findViewById(R.id.search)

            viewPager.registerOnPageChangeCallback(onPageChangeCallback(viewPager, tabAdapter))

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })
        }
    }

    private fun onPageChangeCallback(viewPager: ViewPager2, adapter: StudentCompletedTabPageAdapter): ViewPager2.OnPageChangeCallback{
        return object : ViewPager2.OnPageChangeCallback(){

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                button = button xor false
            }
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
        outState.putBoolean("college_page_is_configuration_changed",true)
        outState.putString("college_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("college_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }
}