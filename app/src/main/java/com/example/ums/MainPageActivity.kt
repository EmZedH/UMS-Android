package com.example.ums

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ums.dialogFragments.ExitDialog
import com.example.ums.dialogFragments.LogOutDialog
import com.example.ums.listener.SearchListener
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.UserDAO
import com.example.ums.viewmodels.MainPageViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainPageActivity: AppCompatActivity(){

    private lateinit var userFragment: Fragment
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String
    private lateinit var navigationView: NavigationView
    private lateinit var user: User
    private lateinit var toolBar: MaterialToolbar
    private lateinit var searchView: SearchView

    private lateinit var mainPageViewModel: MainPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)
        mainPageViewModel = ViewModelProvider(this)[MainPageViewModel::class.java]
        val userDAO = UserDAO(DatabaseHelper(this))

        toolBar = findViewById(R.id.top_app_bar)
        setSupportActionBar(toolBar)
        val bundle = intent.extras

        val userID = bundle!!.getInt("userID")
        user = userDAO.get(userID)!!
        userRole = bundle.getString("userRole")!!

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
        if(mainPageViewModel.isSearchViewOpen.value==null){
            mainPageViewModel.setSearchView(true)
        }
        if(userRole == "SUPER_ADMIN"){
            superAdminProcesses()
        }
    }

    private fun superAdminProcesses() {
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view).append(" ${user.name}")
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id).append(" SA/${user.id}")

        userFragment = SuperAdminMainPage()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.super_admin_fragment_container, userFragment)
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.search -> {
                toolBar.menu.clear()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(userRole=="SUPER_ADMIN") {
            menuInflater.inflate(R.menu.top_app_bar, menu)
            val menuItem = menu.findItem(R.id.search)
            searchView = menuItem.actionView as SearchView
            searchView.queryHint = getString(R.string.search)
            searchView.isIconified = mainPageViewModel.isSearchViewOpen.value!!
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    mainPageViewModel.setQuery(p0!!)
                    (userFragment as SearchListener).onSearch(p0)
                    return false
                }
            })
        }
        if(mainPageViewModel.query.value!=null){
            searchView.setQuery(mainPageViewModel.query.value, true)
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START) || !searchView.isIconified) {
            drawerLayout.closeDrawer(GravityCompat.START)
            searchView.isIconified = true
        }
        else{
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val exitDialogFragment = ExitDialog()
        exitDialogFragment.show(supportFragmentManager, "ExitDialog")
    }

    override fun onResume() {
        super.onResume()
        user = UserDAO(DatabaseHelper(this)).get(user.id)!!
        val welcomeTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view)
        welcomeTextView.setText(R.string.hi_string)
        welcomeTextView.append(" ${user.name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPageViewModel.setSearchView(searchView.isIconified)
    }
}