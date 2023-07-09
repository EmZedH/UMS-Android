package com.example.ums.professorActivities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.UserRole
import com.example.ums.adapters.ProfessorMainPageListItemViewAdapter
import com.example.ums.dialogFragments.ExitDialog
import com.example.ums.dialogFragments.LogOutDialog
import com.example.ums.interfaces.ClickListener
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.example.ums.model.databaseAccessObject.UserDAO
import com.example.ums.superAdminCollegeAdminActivities.ManageProfileActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class ProfessorMainPageActivity: AppCompatActivity(), ClickListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String
    private lateinit var navigationView: NavigationView
    private var user: User? = null
    private var isConfigurationChanged: Boolean? = null
    private lateinit var toolBar: MaterialToolbar
    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var isSearchViewOpen: Boolean = true
    private var userID: Int? = null

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private var professorsCoursesListItemViewAdapter: ProfessorMainPageListItemViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        if(savedInstanceState!=null){
            searchQuery = savedInstanceState.getString("main_page_activity_search_query")
            isSearchViewOpen = savedInstanceState.getBoolean("main_page_activity_is_search_query_open")

            isConfigurationChanged = savedInstanceState.getBoolean("professor_courses_is_configuration_changed")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.professor_main_page_layout)

        firstTextView = findViewById(R.id.no_item_text_view)
        secondTextView = findViewById(R.id.second_text_view)

        firstTextView.text = getString(R.string.no_courses_string)
        secondTextView.text = getString(R.string.please_wait_admin_yet_to_add_courses_string)

        val recyclerView: RecyclerView = findViewById(R.id.list_view)


        val databaseHelper = DatabaseHelper.newInstance(this)
        val userDAO = UserDAO(databaseHelper)
        val bundle = intent.extras
        val userID = bundle?.getInt("userID")
        this.userID = userID
        onRefresh()

        user = userID?.let { userDAO.get(it) }
        setView(userID, userDAO, bundle)

        val courseProfessorDAO = CourseProfessorDAO(databaseHelper)
        val professorDAO = ProfessorDAO(databaseHelper)
        professorsCoursesListItemViewAdapter = ProfessorMainPageListItemViewAdapter(userID!! , courseProfessorDAO, professorDAO, this)
        recyclerView.adapter = professorsCoursesListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        searchView?.queryHint = getString(R.string.search)
        if(isConfigurationChanged==true){
            searchView?.isIconified = isSearchViewOpen
            isConfigurationChanged = null
        }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchQuery = query
                professorsCoursesListItemViewAdapter?.filter(query)
                return false
            }
        })
        searchView?.setQuery(searchQuery, true)
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view).append(" ${user?.name}")
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_user_id).append(" P/${user?.id}")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                onBack()
            }
        })


    }

    private fun setView(userID: Int?, userDAO: UserDAO, bundle: Bundle?){
        userID ?: return
        toolBar = findViewById(R.id.top_app_bar)
        setSupportActionBar(toolBar)
        user = userDAO.get(userID) ?: return
        userRole = bundle?.getString("userRole")!!

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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(userRole== UserRole.PROFESSOR.role) {
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
                    professorsCoursesListItemViewAdapter?.filter(p0)
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
        exitDialogFragment.show(supportFragmentManager, "ExitDialog")
    }

    override fun onResume() {
        super.onResume()
        val databaseHelper = DatabaseHelper.newInstance(this)
        user = UserDAO(databaseHelper).get(user?.id ?: return)
        val welcomeTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_welcome_text_view)
        welcomeTextView.setText(R.string.hi_string)
        welcomeTextView.append(" ${user?.name}")
    }

    private fun onRefresh(){
        val databaseHelper = DatabaseHelper.newInstance(this)
        val courseProfessorDAO = CourseProfessorDAO(databaseHelper)
        if(courseProfessorDAO.getList(this.userID).isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("main_page_activity_search_query",searchQuery)
        outState.putBoolean("professor_courses_is_configuration_changed",true)
        if(searchView!=null){
            outState.putBoolean("main_page_activity_is_search_query_open",searchView!!.isIconified)
        }
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(this, ProfessorStudentsListActivity::class.java)
        intent.putExtras(bundle ?: return)
        startActivity(intent)
    }
}