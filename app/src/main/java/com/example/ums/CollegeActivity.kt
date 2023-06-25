package com.example.ums

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.viewpager2.widget.ViewPager2
import com.example.ums.adapters.CollegeTabPageAdapter
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
    private var isConfigurationChanged: Boolean? = null
    private var toolBar: MaterialToolbar? = null
    private var collegeID: Int? = null
    private var button: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbed_page_layout)

        if(savedInstanceState!=null){
            isConfigurationChanged = savedInstanceState.getBoolean("college_page_is_configuration_changed")
            searchQuery = savedInstanceState.getString("college_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("college_page_activity_is_search_query_open")
            collegeID = savedInstanceState.getInt("college_page_activity_college_id")
        }
        val addFloatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val collegeDAO = CollegeDAO(DatabaseHelper(this))
        val bundle = intent.extras
        collegeID = bundle?.getInt("collegeID")
        val collegeID = collegeID
        if(collegeID!=null){
            val college = collegeDAO.get(collegeID)
            toolBar = findViewById(R.id.top_app_bar)
            toolBar?.title = college?.name
            toolBar?.setNavigationOnClickListener {
                if(searchView!=null){
                    if(!searchView!!.isIconified){
                        searchView?.isIconified = true
                        return@setNavigationOnClickListener
                    }
                }
                finish()
            }
            val fragments: List<AddableSearchableFragment> = listOf(DepartmentFragment(), CollegeAdminFragment() )
            val tabAdapter = CollegeTabPageAdapter(this, collegeID, fragments)
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
            val infoButton = findViewById<ActionMenuItemView>(R.id.info)

            infoButton.setOnClickListener {
                val intent = Intent(this, CollegeDetailsActivity::class.java)
                val collegeDetailsBundle = Bundle().apply {
                    putInt("college_details_college_id", collegeID)
                }
                intent.putExtras(collegeDetailsBundle)
                startActivity(intent)
            }

            viewPager.registerOnPageChangeCallback(onPageChangeCallback(addFloatingActionButton, viewPager, tabAdapter))

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBack()
                }
            })
        }
    }

    private fun onPageChangeCallback(addFloatingActionButton: FloatingActionButton, viewPager: ViewPager2, adapter: CollegeTabPageAdapter): ViewPager2.OnPageChangeCallback{
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
        outState.putBoolean("college_page_is_configuration_changed",true)
        outState.putString("college_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("college_page_activity_is_search_query_open",searchView!!.isIconified)
        }
        if(collegeID!=null){
            outState.putInt("college_page_activity_college_id", collegeID!!)
        }
    }

    override fun onResume() {
        super.onResume()

        if(collegeID!=null){
            toolBar?.title = CollegeDAO(DatabaseHelper(this)).get(collegeID!!)?.name
        }
    }
}