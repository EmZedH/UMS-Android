package com.example.ums.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.adapters.CollegeListItemViewAdapter
import com.example.ums.adapters.SelectableListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CollegeAddBottomSheet
import com.example.ums.bottomsheetdialogs.CollegeUpdateBottomSheet
import com.example.ums.dialogFragments.CollegeDeleteDialog
import com.example.ums.interfaces.LatestItemListener
import com.example.ums.interfaces.SelectionListener
import com.example.ums.model.SelectionItem
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.superAdminCollegeAdminActivities.CollegeActivity

class SuperAdminMainPageFragment : LatestListFragment(), LatestItemListener, SelectionListener {

    private lateinit var addCollegeBottomSheet : CollegeAddBottomSheet
    private var collegeListItemViewAdapter : CollegeListItemViewAdapter? = null
    private var selectableListItemViewAdapter: SelectableListItemViewAdapter? = null
    private lateinit var collegeDAO: CollegeDAO
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var recyclerView: RecyclerView? = null
    private var editCollegeId: Int? = null

    private var selectedItems: MutableList<String> = mutableListOf()
    private var isSelectionEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))
        collegeListItemViewAdapter = CollegeListItemViewAdapter(collegeDAO, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(savedInstanceState!=null){
            val idStringList = savedInstanceState.getStringArray("selected_items_id_string_list") ?: arrayOf()
            isSelectionEnabled = savedInstanceState.getBoolean("is_selection_enabled")
            for(index in idStringList.indices){
                selectedItems.add(
                    idStringList[index]
                )
            }
        }

        val view = inflater.inflate(R.layout.fragment_list_page, container, false)
        recyclerView = view.findViewById(R.id.list_view)

        firstTextView = view.findViewById(R.id.no_items_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)

        addCollegeBottomSheet = CollegeAddBottomSheet()

        if(collegeDAO.getList().isNotEmpty()){

            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.setText(R.string.no_colleges_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
        if(selectedItems.isNotEmpty() && isSelectionEnabled){
            val selectList: MutableList<SelectionItem> = mutableListOf()
            for (collegeItem in collegeDAO.getList()){
                selectList.add(SelectionItem(Utility.idsToString(intArrayOf(collegeItem.id)), "CID : C/${collegeItem.id}", collegeItem.name))
            }
            selectableListItemViewAdapter = SelectableListItemViewAdapter(selectedItems, selectList, this , collegeDAO)
            recyclerView?.adapter = selectableListItemViewAdapter
        }
        else{
            recyclerView?.adapter = collegeListItemViewAdapter
        }
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("collegeAddBottomSheet"){_, result->
            val id = result.getInt("id")
            addAt(id)
        }
        setFragmentResultListener("collegeDeleteDialog"){_, result->
            val id = result.getInt("collegeID")
            collegeListItemViewAdapter?.deleteItem(id)
            onRefresh()
        }
        setFragmentResultListener("CollegeUpdateBottomSheet"){_, result->
            val id = result.getInt("collegeID")
            collegeListItemViewAdapter?.updateItemInAdapter(id)
        }
    }

    override fun onAdd() {
        addCollegeBottomSheet.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    private fun addAt(id: Int) {
        collegeListItemViewAdapter?.addItem(id)
        onRefresh()
    }

    override fun onSearch(query: String?) {
        recyclerView?.adapter = collegeListItemViewAdapter
        val list = collegeListItemViewAdapter?.filter(query)
        if(selectedItems.isNotEmpty() && !list.isNullOrEmpty() && isSelectionEnabled){

            val selectList: MutableList<SelectionItem> = mutableListOf()
            for (collegeItem in list){
                selectList.add(SelectionItem(Utility.idsToString(intArrayOf(collegeItem.id)), "CID : C/${collegeItem.id}", collegeItem.name))
            }
            selectableListItemViewAdapter = SelectableListItemViewAdapter(selectedItems, selectList, this, collegeDAO)
            recyclerView?.adapter = selectableListItemViewAdapter
        }
        else{
            selectableListItemViewAdapter = null
        }
    }

    override fun clearSelection() {
        isSelectionEnabled = false
        selectableListItemViewAdapter = null
        collegeListItemViewAdapter?.updateList()
//        collegeListItemViewAdapter = CollegeListItemViewAdapter(collegeDAO, this)
        recyclerView?.adapter = collegeListItemViewAdapter
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    override fun selectionCount(size: Int) {
        setFragmentResult("FragmentSelectionCount", bundleOf("selected_count" to size))
    }

    override fun onUpdate(id: Int) {
        val editFragment = CollegeUpdateBottomSheet.newInstance(id)
        editFragment?.show((context as AppCompatActivity).supportFragmentManager, "updateBottomSheetDialog")
    }

    override fun onDelete(id: Int) {
        val deleteFragment = CollegeDeleteDialog()
        deleteFragment.setCollegeID(id)
        deleteFragment.show((context as AppCompatActivity).supportFragmentManager, "deleteDialog")
    }

    override fun onClick(bundle: Bundle?) {
        val intent = Intent(requireContext(), CollegeActivity::class.java)
        if(bundle!=null){
            editCollegeId = bundle.getInt("collegeID")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun switchToolbar(shouldSwitchToolbar: Boolean) {
        setFragmentResult("FragmentSwitchToolbar", bundleOf("switch_toolbar" to shouldSwitchToolbar))
    }

    override fun onLongClick(selectedItemId: String, itemList: MutableList<SelectionItem>) {
        isSelectionEnabled = true
        selectableListItemViewAdapter = SelectableListItemViewAdapter(mutableListOf(selectedItemId),itemList, this , collegeDAO)
        recyclerView?.adapter = selectableListItemViewAdapter
        selectionCount(1)
    }

    override fun deleteAll() {
        selectableListItemViewAdapter?.deleteAll()
        clearSelection()
    }

    override fun selectAll() {
        selectableListItemViewAdapter?.selectAll()
    }

    private fun onRefresh(){
        if(collegeDAO.getList().isNotEmpty()){
            firstTextView.visibility = View.INVISIBLE
            secondTextView.visibility = View.INVISIBLE
        }
        else{
            firstTextView.setText(R.string.no_colleges_string)
            firstTextView.visibility = View.VISIBLE
            secondTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if(editCollegeId!=null){
            collegeListItemViewAdapter?.updateItemInAdapter(editCollegeId!!)
            editCollegeId=null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val adapter = selectableListItemViewAdapter
        outState.putBoolean("is_selection_enabled", isSelectionEnabled)
        if(adapter!=null){
            outState.putStringArray("selected_items_id_string_list", adapter.selectedItemsIds.toTypedArray())
        }
    }
}

