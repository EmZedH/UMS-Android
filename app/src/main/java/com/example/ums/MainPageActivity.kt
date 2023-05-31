package com.example.ums

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainPageActivity: AppCompatActivity(), FragmentRefreshListener{

    private lateinit var userFragment: Fragment
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String
    private lateinit var navigationView: NavigationView
    private lateinit var user: User
    private lateinit var toolBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        val userDAO = UserDAO(DatabaseHelper(this))

        toolBar = findViewById(R.id.top_app_bar)
        setSupportActionBar(toolBar)

        val bundle = intent.extras

        val userID = bundle!!.getInt("userID")
        user = userDAO.get(userID)!!
        userRole = bundle.getString("userRole")!!

        val collegeDAO = CollegeDAO(DatabaseHelper(this))
        drawerLayout = findViewById(R.id.main_page_drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        toggle.syncState()

        navigationView = findViewById(R.id.navigation_view)


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.manage_profile_tab -> {
                    val intent = Intent(this, ManageProfileActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                R.id.log_out_tab -> {
                    showLogOutConfirmation(this)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        if(userRole == "SUPER_ADMIN"){
            superAdminProcesses(collegeDAO)
        }
    }

    private fun superAdminProcesses(collegeDAO: CollegeDAO){

        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view).append(" ${user.name}")
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id).append(" SA/${user.id}")

        userFragment = SuperAdminMainPage()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.super_admin_fragment_container, userFragment)
            commit()
        }

        val bottomSheet = AddCollegeBottomSheet(collegeDAO, this)

        val addFloatingButton = findViewById<FloatingActionButton>(R.id.add_floating_action_button)

        addFloatingButton.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "bottomSheetDialog")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                toolBar.menu.clear()
                true
            }
            // Handle other menu items here
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(userRole=="SUPER_ADMIN") {
            menuInflater.inflate(R.menu.top_app_bar, menu)
            val menuItem = menu.findItem(R.id.search)
            val searchView = menuItem.actionView as SearchView
            searchView.queryHint = getString(R.string.search)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }

            })
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
            showExitConfirmationDialog(this)
        }
    }
    private fun showExitConfirmationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)

        // Set the dialog title and message
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to exit?")

        // Set the positive button (delete)
        builder.setPositiveButton("Confirm") { _, _ ->
            // Perform the delete operation
            finish()
        }

        // Set the negative button (cancel)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun showLogOutConfirmation(context: Context){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Log Out").setMessage("Are you sure you want to Log Out")

        builder.setPositiveButton("Confirm"){ _, _ ->
            val editor = getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE).edit()
            editor.putBoolean("isLoggedOut", true)
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        // Set the negative button (cancel)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    override fun refreshFragment() {
        supportFragmentManager.beginTransaction().detach(userFragment).commit()
        supportFragmentManager.beginTransaction().attach(userFragment).commit()
    }
}