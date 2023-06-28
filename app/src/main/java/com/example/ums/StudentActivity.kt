package com.example.ums

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.adapters.StudentTabPageAdapter
import com.example.ums.fragments.AddableSearchableFragment
import com.example.ums.fragments.StudentOpenElectiveFragment
import com.example.ums.fragments.StudentProfessionalElectiveFragment
import com.example.ums.fragments.TransactionFragment
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StudentActivity: AppCompatActivity() {

    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private var studentID: Int? = null
    private var button: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbed_page_with_info_button_layout)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("student_activity_is_configuration_changed")
            searchQuery = savedInstanceState.getString("student_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("student_activity_is_search_query_open")
            studentID = savedInstanceState.getInt("student_activity_college_id")
        }
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val studentDAO = StudentDAO(DatabaseHelper(this))
        val bundle = intent.extras
        studentID = bundle?.getInt("student_activity_student_id")

        val studentID = studentID
        if(studentID!=null){
            val student = studentDAO.get(studentID)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = student?.user?.name
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            val fragments: List<AddableSearchableFragment> = listOf(StudentProfessionalElectiveFragment(), StudentOpenElectiveFragment(), TransactionFragment())
            val tabAdapter = StudentTabPageAdapter(this, studentID, fragments)
            val viewPager = findViewById<ViewPager2>(R.id.view_pager)
            val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

            viewPager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, viewPager){tab, position->
                when(position){
                    0-> tab.text = "Professional Courses"
                    1-> tab.text = "Open Courses"
                    2-> tab.text = "Transactions"
                }
                viewPager.adapter
            }.attach()
            searchView = findViewById(R.id.search)
            val infoButton = findViewById<ActionMenuItemView>(R.id.info)

            infoButton.setOnClickListener {
                val intent = Intent(this, StudentDetailsActivity::class.java)
                val departmentDetailsBundle = Bundle().apply {
                    putInt("student_details_student_id", studentID)
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

    private fun onPageChangeCallback(addFloatingActionButton: FloatingActionButton, viewPager: ViewPager2, adapter: StudentTabPageAdapter): ViewPager2.OnPageChangeCallback{
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
                    if (selectedFragment is AddableSearchableFragment) {
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
        outState.putBoolean("student_activity_is_configuration_changed",true)
        outState.putString("student_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("student_activity_is_search_query_open",searchView!!.isIconified)
        }
        if(studentID!=null){
            outState.putInt("student_activity_college_id", studentID!!)
        }
    }

    override fun onResume() {
        super.onResume()
        toolBar?.title = "${StudentDAO(DatabaseHelper(this)).get(studentID)?.user?.name} Records"
    }
}