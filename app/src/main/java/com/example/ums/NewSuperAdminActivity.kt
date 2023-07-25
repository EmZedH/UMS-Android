package com.example.ums

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ums.dialogFragments.ExitDialog
import com.example.ums.fragments.LatestListFragment
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.UserDAO
import com.example.ums.ui.theme.UMSTheme
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NewSuperAdminActivity : AppCompatActivity() {

    var userFragment: LatestListFragment? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userRole : String
//    private lateinit var navigationView: NavigationView
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
        super.onCreate(savedInstanceState)

        searchQuery = savedInstanceState?.getString("main_page_activity_search_query")
        isSearchViewOpen = savedInstanceState?.getBoolean("main_page_activity_is_search_query_open")
        isSelectionToolbarOpen = savedInstanceState?.getBoolean("main_page_activity_is_selection_toolbar_open")
        selectionNumber = savedInstanceState?.getInt("main_page_activity_selection_number") ?: 0
        val databaseHelper = DatabaseHelper.newInstance(this)
        val userDAO = UserDAO(databaseHelper)
        val bundle = intent.extras
        val userID = bundle?.getInt("userID")
        user = userDAO.get(userID) ?: return
        setContent {
            UMSTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationDrawer(user)
                }
            }
        }

        floatingActionButton = findViewById(R.id.floating_action_button)
        floatingActionButton?.setOnClickListener {
            userFragment?.onAdd()
        }

//        setView(userID, userDAO, bundle)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                onBack()
            }
        })


        if(supportFragmentManager.fragments.isNotEmpty()){
            userFragment = supportFragmentManager.findFragmentByTag("LatestListFragment") as LatestListFragment?
        }
        else if(userRole == UserRole.SUPER_ADMIN.role){
//            superAdminProcesses()
        }
        else if(userRole == UserRole.COLLEGE_ADMIN.role){
//            val collegeAdminDAO = CollegeAdminDAO(databaseHelper)
//            collegeAdminProcesses(collegeAdminDAO.get(userID ?: return)?.collegeID)
        }
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

    private fun switchBackToolbar(){
        if (selectionToolbar.visibility == View.VISIBLE) {
            userFragment?.clearSelection()
            floatingActionButton?.visibility = View.VISIBLE
            selectionToolbar.visibility = View.GONE
            toolBar.visibility = View.VISIBLE
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(user: User) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                id = user.id,
                name = user.name,
                role = user.role,
                drawerState = drawerState,
                scope = scope
            )},
        content = {
            PageContent(userRole = user.role) { scope.launch { drawerState.open() } }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerSheet(id: Int, name: String, role: String, drawerState: DrawerState, scope: CoroutineScope){
    val items = listOf(stringResource(id = R.string.manage_profile_string), stringResource(id = R.string.log_out_string))
    val selectedItem = rememberSaveable { mutableStateOf(items[0]) }
    ModalDrawerSheet {
        Spacer(Modifier.height(50.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.hi_user, name),
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = when(role){
                UserRole.SUPER_ADMIN.role -> stringResource(id = R.string.super_admin_user_id, id)
                UserRole.COLLEGE_ADMIN.role -> stringResource(id = R.string.college_admin_user_id, id)
                UserRole.PROFESSOR.role -> stringResource(id = R.string.professor_user_id, id)
                UserRole.STUDENT.role -> stringResource(id = R.string.student_user_id, id)
                else -> "" },
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = stringResource(id = R.string.person_image_string),
            modifier = Modifier
                .fillMaxWidth()
                .size(100.dp)
        )
        Spacer(Modifier.height(50.dp))

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item) },
                selected = item == selectedItem.value,
                onClick = {
                    scope.launch { drawerState.close() }
                    selectedItem.value = item
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageContent(userRole: String, onNavigationClick: () -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = when(userRole){
                        UserRole.SUPER_ADMIN.role -> stringResource(id = R.string.colleges_string)
                        UserRole.COLLEGE_ADMIN.role -> stringResource(id = R.string.departments_string)
                        else -> ""
                    })
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationClick) {
                        Icon(Icons.Filled.Menu, stringResource(id = R.string.navigation_drawer_open))
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                },
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_string))
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun DrawerPreview(){
    NavigationDrawer(User(1,"Muhamed","2020202020","2001-04-05", "M", "CHENNAI", "EASY", UserRole.COLLEGE_ADMIN.role, "muhamed@email.com"))
}