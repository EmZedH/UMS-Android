package com.example.ums

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainPageActivity: AppCompatActivity(){

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        val toolBar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolBar)

        val bundle = intent.extras

//        val userName = bundle!!.getString("userName")
//        val userID = bundle.getString("userID")
        userRole = bundle!!.getString("userRole")!!

        val collegeDAO = CollegeDAO(this)
        drawerLayout = findViewById(R.id.main_page_drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()

        if(userRole == "SUPER_ADMIN"){
            superAdminProcesses(collegeDAO)
        }
    }

    private fun superAdminProcesses(collegeNewDAO: CollegeDAO){

        val superAdminFragment = SuperAdminMainPage()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.super_admin_fragment_container, superAdminFragment)
            commit()
        }

        val bottomSheet = AddCollege(collegeNewDAO, superAdminFragment)

        val addFloatingButton = findViewById<FloatingActionButton>(R.id.add_floating_action_button)

        addFloatingButton.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "bottomSheetDialog")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                // Handle search action here
                true
            }
            // Handle other menu items here
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(userRole=="SUPER_ADMIN") {
            menuInflater.inflate(R.menu.top_app_bar, menu)
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Close the Navigation Drawer when the back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            finish()
        }
    }
}