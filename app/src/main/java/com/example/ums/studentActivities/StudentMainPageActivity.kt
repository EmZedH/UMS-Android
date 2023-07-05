package com.example.ums.studentActivities

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.SearchableFragment
import com.example.ums.adapters.StudentCoursesTabAdapter
import com.example.ums.dialogFragments.LogOutDialog
import com.example.ums.fragments.StudentProfileOpenElectiveFragment
import com.example.ums.fragments.StudentProfileProfessionalElectiveFragment
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.superAdminCollegeAdminActivities.ManageProfileActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StudentMainPageActivity: AppCompatActivity() {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null

    private var userID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_main_page_layout)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("student_main_page_is_configuration_changed")
            searchQuery = savedInstanceState.getString("student_main_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("student_main_page_activity_is_search_query_open")
            userID = savedInstanceState.getInt("student_main_page_activity_college_id")
        }

        val studentDAO = StudentDAO(DatabaseHelper(this))
        val bundle = intent.extras
        userID = bundle?.getInt("userID")
        val userID = userID
        if(userID!=null){
            val student = studentDAO.get(userID)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = "Home"

            val drawerLayout = findViewById<DrawerLayout>(R.id.main_page_drawer_layout)
            toolBar?.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            val navigationView = findViewById<NavigationView>(R.id.navigation_view)

            navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view).append(" ${student?.user?.name}")
            navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id).append(" S/${student?.user?.id}")

            navigationView.setNavigationItemSelectedListener { menuItem ->

                when(menuItem.itemId){
                    R.id.manage_profile_tab -> {
                        val intent = Intent(this, ManageProfileActivity::class.java)
                        this.intent.extras?.let { intent.putExtras(it) }
                        startActivity(intent)
                    }
                    R.id.pay_fees_tab ->{
                        val intent = Intent(this, StudentTransactionPage::class.java)
                        this.intent.extras?.let { intent.putExtras(it) }
                        startActivity(intent)
                    }
                    R.id.log_out_tab -> {
                        val logOutDialog = LogOutDialog()
                        logOutDialog.show(supportFragmentManager, "Log Out Dialog")
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            val fragments: List<SearchableFragment> = listOf(StudentProfileProfessionalElectiveFragment(), StudentProfileOpenElectiveFragment() )
            val tabAdapter = StudentCoursesTabAdapter(this, userID, fragments)
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

    private fun onPageChangeCallback(viewPager: ViewPager2, adapter: StudentCoursesTabAdapter): ViewPager2.OnPageChangeCallback{
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
                    if (selectedFragment is SearchableFragment) {
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
        outState.putBoolean("student_main_page_is_configuration_changed",true)
        outState.putString("student_main_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("student_main_page_activity_is_search_query_open",searchView!!.isIconified)
        }
        if(userID!=null){
            outState.putInt("student_main_page_activity_college_id", userID!!)
        }
    }
}