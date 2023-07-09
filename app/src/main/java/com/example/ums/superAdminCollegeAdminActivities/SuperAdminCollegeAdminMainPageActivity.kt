package com.example.ums.superAdminCollegeAdminActivities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.UserRole
import com.example.ums.dialogFragments.ExitDialog
import com.example.ums.dialogFragments.LogOutDialog
import com.example.ums.dialogFragments.SelectionDeleteDialog
import com.example.ums.fragments.CollegeAdminMainPageFragment
import com.example.ums.fragments.LatestListFragment
import com.example.ums.fragments.SuperAdminMainPageFragment
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class SuperAdminCollegeAdminMainPageActivity: AppCompatActivity(){

    var userFragment: LatestListFragment? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String
    private lateinit var navigationView: NavigationView
    private lateinit var user: User
    private lateinit var toolBar: MaterialToolbar
    private lateinit var selectionToolbar: MaterialToolbar
    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean? = true

    private var floatingActionButton: FloatingActionButton? = null
    private var isSelectionToolbarOpen: Boolean? = false

    private var selectionNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        searchQuery = savedInstanceState?.getString("main_page_activity_search_query")
        isSearchViewOpen = savedInstanceState?.getBoolean("main_page_activity_is_search_query_open")
        isSelectionToolbarOpen = savedInstanceState?.getBoolean("main_page_activity_is_selection_toolbar_open")
        selectionNumber = savedInstanceState?.getInt("main_page_activity_selection_number") ?: 0

        super.onCreate(savedInstanceState)
        setContentView(R.layout.super_college_admin_main_page)

        val databaseHelper = DatabaseHelper.newInstance(this)
        val userDAO = UserDAO(databaseHelper)
        val bundle = intent.extras
        val userID = bundle?.getInt("userID")

        floatingActionButton = findViewById(R.id.floating_action_button)
        floatingActionButton?.setOnClickListener {
            userFragment?.onAdd()
        }

        setView(userID, userDAO, bundle)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                onBack()
            }
        })


        if(supportFragmentManager.fragments.isNotEmpty()){
            userFragment = supportFragmentManager.findFragmentByTag("LatestListFragment") as LatestListFragment?
        }
        else if(userRole == UserRole.SUPER_ADMIN.role){
            superAdminProcesses()
        }
        else if(userRole == UserRole.COLLEGE_ADMIN.role){
            val collegeAdminDAO = CollegeAdminDAO(databaseHelper)
            collegeAdminProcesses(collegeAdminDAO.get(userID ?: return)?.collegeID)
        }
    }

    private fun setView(userID: Int?, userDAO: UserDAO, bundle: Bundle?){
        userID ?: return
        toolBar = findViewById(R.id.top_app_bar)
        selectionToolbar = findViewById(R.id.selection_app_bar)
        setSupportActionBar(toolBar)
        user = userDAO.get(userID) ?: return
        userRole = user.role
        toolBar.title = if(userRole == UserRole.SUPER_ADMIN.role) getString(R.string.colleges_string) else getString(R.string.departments_string)
        if(isSelectionToolbarOpen == true){
            selectionToolbar.title = getString(R.string.selected_number, selectionNumber)
            switchToSelectionToolbar()
        }
        supportFragmentManager.setFragmentResultListener("FragmentSelectionCount", this){_, result->
            selectionNumber = result.getInt("selected_count")
            selectionToolbar.title = getString(R.string.selected_number, selectionNumber)
        }
        supportFragmentManager.setFragmentResultListener("FragmentSwitchToolbar", this){_, result->
            val shouldSwitchToolbar = result.getBoolean("switch_toolbar")
            if(shouldSwitchToolbar){
                switchToSelectionToolbar()
            }
            else{
                switchBackToolbar()
            }
        }
        supportFragmentManager.setFragmentResultListener("SelectionDeleteDialog", this){_, _ ->
            userFragment?.deleteAll()
            switchBackToolbar()
        }

        drawerLayout = findViewById(R.id.main_page_drawer_layout)

        toolBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        selectionToolbar.setNavigationOnClickListener {
            switchBackToolbar()
        }

        val deleteAllButton = selectionToolbar.menu.findItem(R.id.delete_toolbar)
        val selectAllButton = selectionToolbar.menu.findItem(R.id.select_all_toolbar)

        deleteAllButton.setOnMenuItemClickListener {
            val selectionDeleteDialog = SelectionDeleteDialog()
            selectionDeleteDialog.show(supportFragmentManager, "SelectionDeleteDialog")
            true
        }

        selectAllButton.setOnMenuItemClickListener {
            userFragment?.selectAll()
            true
        }

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.manage_profile_tab -> {
                    val intent = Intent(this, ManageProfileActivity::class.java)
                    if(bundle!=null){
                        intent.putExtras(bundle)
                    }
                    startActivity(intent)
                }
                R.id.log_out_tab -> {
                    val logOutDialog = LogOutDialog()
                    logOutDialog.show(supportFragmentManager, "LogOutDialog")
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun superAdminProcesses() {
        userFragment = SuperAdminMainPageFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, userFragment ?: return, "LatestListFragment")
            commit()
        }
    }

    private fun collegeAdminProcesses(collegeID: Int?){

        userFragment = CollegeAdminMainPageFragment()
        userFragment?.arguments = Bundle().apply {
            putInt("college_id", collegeID ?: return)
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, userFragment ?: return, "LatestListFragment")
            commit()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(userRole== UserRole.SUPER_ADMIN.role || userRole == UserRole.COLLEGE_ADMIN.role) {

            menuInflater.inflate(R.menu.search_menu, menu)
            val menuItem = menu.findItem(R.id.search)
            searchView = menuItem.actionView as SearchView
            searchView?.queryHint = getString(R.string.search)
            searchView?.isIconified = isSearchViewOpen ?: true
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
            searchView?.clearFocus()
        }
        return true
    }

    private fun onBack(){
        if(selectionToolbar.isVisible){
            switchBackToolbar()
            return
        }
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
        exitDialogFragment.show(supportFragmentManager, "ExitDialog")
    }
    override fun onResume() {
        super.onResume()
        val databaseHelper = DatabaseHelper.newInstance(this)
        user = UserDAO(databaseHelper).get(user.id)!!
        val welcomeTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view)
        val userIDTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id)
        welcomeTextView.text = getString(R.string.hi_user, user.name)
        if(user.role == UserRole.SUPER_ADMIN.role){
            userIDTextView.text = getString(R.string.super_admin_user_id, user.id)
        }
        else if(user.role == UserRole.COLLEGE_ADMIN.role){
            userIDTextView.text = getString(R.string.college_admin_user_id, user.id)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("main_page_activity_search_query",searchQuery)
        outState.putBoolean("main_page_activity_is_selection_toolbar_open", selectionToolbar.isVisible)
        outState.putInt("main_page_activity_selection_number", selectionNumber)
        if(searchView!=null){
            outState.putBoolean("main_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }

    private fun switchToSelectionToolbar() {
        if (toolBar.visibility == View.VISIBLE) {
            toolBar.visibility = View.GONE
            selectionToolbar.visibility = View.VISIBLE
            floatingActionButton?.visibility = View.GONE
        }
    }

    private fun switchBackToolbar(){
        if (selectionToolbar.visibility == View.VISIBLE) {
            userFragment?.clearSelection()
            floatingActionButton?.visibility = View.VISIBLE
            selectionToolbar.visibility = View.GONE
            toolBar.visibility = View.VISIBLE
        }
    }
}