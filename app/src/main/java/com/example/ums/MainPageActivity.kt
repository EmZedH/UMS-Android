package com.example.ums

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainPageActivity: AppCompatActivity(){
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomSheetDialog: BottomSheetDialog


    private lateinit var userRole : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        val toolBar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolBar)

        val bundle = intent.extras

        val userName = bundle!!.getString("userName")
        val userID = bundle.getString("userID")
        userRole = bundle.getString("userRole")!!

        val dbHelper = DatabaseHelper(this)
        val collegeDAO = CollegeDAO(dbHelper)

        drawerLayout = findViewById(R.id.main_page_drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        val superAdminFragment = SuperAdminMainPage()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view, superAdminFragment)
            commit()
        }

        bottomSheetDialog(collegeDAO, superAdminFragment)

        toggle.syncState()


        val addFloatingButton = findViewById<FloatingActionButton>(R.id.add_floating_action_button)

        addFloatingButton.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun bottomSheetDialog(
        collegeDAO: CollegeDAO,
        superAdminFragment: SuperAdminMainPage
    ) {
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.add_item_bottom_sheet)
        // Set the desired behavior for the BottomSheetDialog

        bottomSheetDialog.behavior.isDraggable = true

        val bottomSheetCloseButton =
            bottomSheetDialog.findViewById<ImageButton>(R.id.add_item_close_button)
        bottomSheetCloseButton!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        val collegeName =
            bottomSheetDialog.findViewById<TextInputLayout>(R.id.college_name_text_field)
        val collegeAddress =
            bottomSheetDialog.findViewById<TextInputLayout>(R.id.college_address_textfield)
        val collegeTelephone =
            bottomSheetDialog.findViewById<TextInputLayout>(R.id.college_telephone_textfield)

        val addCollegeButton =
            bottomSheetDialog.findViewById<MaterialButton>(R.id.add_college_button)

        collegeName?.error = null
        collegeAddress?.error = null
        collegeTelephone?.error = null

        bottomSheetDialog.findViewById<TextView>(R.id.college_id)!!.text =
            "CID : C/${collegeDAO.getNewID()}"

        addCollegeButton?.setOnClickListener {

            if (collegeName?.editText?.text.toString().isEmpty()) {
                collegeName?.error = "Don't leave name field blank"
            }
            if (collegeAddress?.editText?.text.toString().isEmpty()) {
                collegeAddress?.error = "Don't leave address field blank"
            }
        }
        if (collegeTelephone?.editText?.text.toString().isEmpty()) {
            collegeTelephone?.error = "Don't leave telephone field blank"
        }
        if (collegeName?.editText?.text.toString()
                .isNotEmpty() and collegeAddress?.editText?.text.toString()
                .isNotEmpty() and collegeTelephone?.editText?.text.toString().isNotEmpty()
        ) {

            collegeDAO.insert(
                College(
                    collegeDAO.getNewID(),
                    collegeName?.editText?.text.toString(),
                    collegeAddress?.editText?.text.toString(),
                    collegeTelephone?.editText?.text.toString()
                )
            )

            bottomSheetDialog.findViewById<TextView>(R.id.college_id)!!.text =
                "CID : C/${collegeDAO.getNewID()}"

            collegeName?.error = null
            collegeAddress?.error = null
            collegeTelephone?.error = null

            supportFragmentManager.beginTransaction().detach(superAdminFragment).commit()
            supportFragmentManager.beginTransaction().attach(superAdminFragment).commit()
            bottomSheetDialog.dismiss()
            collegeName?.editText?.text?.clear()
            collegeAddress?.editText?.text?.clear()
            collegeTelephone?.editText?.text?.clear()

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