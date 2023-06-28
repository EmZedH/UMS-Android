package com.example.ums

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ums.dialogFragments.ExitDialog
import com.example.ums.dialogFragments.LogOutDialog
import com.example.ums.fragments.AddableSearchableFragment
import com.example.ums.fragments.CollegeAdminMainPageFragment
import com.example.ums.fragments.SuperAdminMainPageFragment
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class SuperAdminCollegeAdminMainPageActivity: AppCompatActivity(){

    var userFragment: AddableSearchableFragment? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String
    private lateinit var navigationView: NavigationView
    private lateinit var user: User
    private lateinit var toolBar: MaterialToolbar
    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {

        if(savedInstanceState!=null){
            searchQuery = savedInstanceState.getString("main_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("main_page_activity_is_search_query_open")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        val userDAO = UserDAO(DatabaseHelper(this))
        val bundle = intent.extras
        val userID = bundle?.getInt("userID")

        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        floatingActionButton.setOnClickListener {
            userFragment?.onAdd()
        }

        setView(userID, userDAO, bundle)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                onBack()
            }
        })

        if(userRole == UserRole.SUPER_ADMIN.role){
            superAdminProcesses()
        }
        if(userRole == UserRole.COLLEGE_ADMIN.role){
            val collegeAdminDAO = CollegeAdminDAO(DatabaseHelper(this))
            collegeAdminProcesses(collegeAdminDAO.get(userID ?: return)?.collegeID)
        }
    }

    private fun setView(userID: Int?, userDAO: UserDAO, bundle: Bundle?){
        userID ?: return
        toolBar = findViewById(R.id.top_app_bar)
        setSupportActionBar(toolBar)
        user = userDAO.get(userID) ?: return
        userRole = bundle?.getString("userRole")!!

        CollegeDAO(DatabaseHelper(this))
        drawerLayout = findViewById(R.id.main_page_drawer_layout)

        toolBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when(menuItem.itemId){
                R.id.manage_profile_tab -> {
                    val intent = Intent(this, ManageProfileActivity::class.java)
                    intent.putExtras(bundle)
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
    }

    private fun superAdminProcesses() {
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view).append(" ${user.name}")
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id).append(" SA/${user.id}")

        userFragment = SuperAdminMainPageFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.super_admin_fragment_container, userFragment ?: return)
            commit()
        }
    }

    private fun collegeAdminProcesses(collegeID: Int?){
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view).append(" ${user.name}")
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id).append(" CA/${user.id}")

        userFragment = CollegeAdminMainPageFragment()
        userFragment?.arguments = Bundle().apply {
            putInt("college_id", collegeID ?: return)
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.super_admin_fragment_container, userFragment ?: return)
            commit()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(userRole==UserRole.SUPER_ADMIN.role) {
            menuInflater.inflate(R.menu.search_menu, menu)
            val menuItem = menu.findItem(R.id.search)
            searchView = menuItem.actionView as SearchView
            searchView?.queryHint = getString(R.string.search)
            searchView?.isIconified = isSearchViewOpen
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    searchQuery = p0
                    userFragment?.onSearch(p0)
                    return false
                }
            })
        }
        if(searchQuery!=null){
            searchView?.setQuery(searchQuery, true)
        }
        return true
    }

    private fun onBack(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if(searchView!=null){
            if(!searchView!!.isIconified){
                searchView?.isIconified = true
                return
            }
        }
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        val exitDialogFragment = ExitDialog()
        exitDialogFragment.show(supportFragmentManager, "exitDialog")
    }

    override fun onResume() {
        super.onResume()
        user = UserDAO(DatabaseHelper(this)).get(user.id)!!
        val welcomeTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view)
        welcomeTextView.setText(R.string.hi_string)
        welcomeTextView.append(" ${user.name}")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("main_page_activity_search_query",searchQuery)
        if(searchView!=null){
            outState.putBoolean("main_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }
}