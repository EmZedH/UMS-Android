package com.example.ums.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.adapters.LongClickableListItemViewAdapter
import com.example.ums.adapters.SelectableListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CollegeAddBottomSheet
import com.example.ums.bottomsheetdialogs.CollegeUpdateBottomSheet
import com.example.ums.dialogFragments.CollegeDeleteDialog
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.interfaces.SelectionListener
import com.example.ums.model.AdapterItem
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.superAdminCollegeAdminActivities.CollegeActivity

class SuperAdminMainPageFragment : LatestListFragment(), SelectionListener, ListIdItemListener {

    private var longClickableListItemViewAdapter: LongClickableListItemViewAdapter? = null

    private var selectableListItemViewAdapter: SelectableListItemViewAdapter? = null
    private lateinit var collegeDAO: CollegeDAO
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var recyclerView: RecyclerView? = null
    private var editCollegeId: Int? = null

    private var selectedItems: MutableList<List<Int>> = mutableListOf()
    private var isSelectionEnabled = false
    private var searchQuery: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        collegeDAO = CollegeDAO(databaseHelper)
        longClickableListItemViewAdapter = LongClickableListItemViewAdapter(getAdapterItemList(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(savedInstanceState!=null){
            val idStringList = savedInstanceState.getStringArray("selected_items_id_string_list") ?: arrayOf()
            searchQuery = savedInstanceState.getString("search_query")
            isSelectionEnabled = savedInstanceState.getBoolean("is_selection_enabled")
            for(index in idStringList.indices){
                selectedItems.add(
                    Utility.stringToIds(idStringList[index])
                )
            }
        }

        val view = inflater.inflate(R.layout.fragment_list_page, container, false)
        recyclerView = view.findViewById(R.id.list_view)

        firstTextView = view.findViewById(R.id.no_items_text_view)
        secondTextView = view.findViewById(R.id.add_to_get_started_text_view)

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
            val selectList: MutableList<AdapterItem> = mutableListOf()
            for (collegeItem in collegeDAO.getList()){
                selectList.add(AdapterItem(listOf(collegeItem.id), getString(R.string.college_id, collegeItem.id), collegeItem.name))
            }
            selectableListItemViewAdapter = SelectableListItemViewAdapter(selectedItems, selectList, this , collegeDAO)
            recyclerView?.adapter = selectableListItemViewAdapter
        }
        else{
//            recyclerView?.adapter = collegeListItemViewAdapter
            recyclerView?.adapter = longClickableListItemViewAdapter
        }
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("collegeAddBottomSheet"){_, result->
            val id = result.getInt("id")
            val college = collegeDAO.get(id)
            college?.let {
                longClickableListItemViewAdapter?.addItem(
                    AdapterItem(
                        listOf(id),
                        getString(R.string.college_id, it.id),
                        college.name
                    )
                )
            }
            onRefresh()
        }
        setFragmentResultListener("collegeDeleteDialog"){_, result->
            val id = result.getInt("collegeID")
            collegeDAO.delete(id)
            longClickableListItemViewAdapter?.deleteItem(listOf(id))
            onRefresh()
        }
        setFragmentResultListener("CollegeUpdateBottomSheet"){_, result->
            val id = result.getInt("collegeID")
            val college = collegeDAO.get(id)
            college?.let {
                longClickableListItemViewAdapter?.updateItem(
                    AdapterItem(
                        listOf(id),
                        getString(R.string.college_id, it.id),
                        college.name
                    )
                )
            }
        }
    }

    override fun onAdd() {
        val addCollegeBottomSheet = CollegeAddBottomSheet()
        addCollegeBottomSheet.show(requireActivity().supportFragmentManager, "bottomSheetDialog")
    }

    override fun onSearch(query: String?) {
        searchQuery = query
        recyclerView?.adapter = longClickableListItemViewAdapter

        val list = longClickableListItemViewAdapter?.filter(query)
        if(selectedItems.isNotEmpty() && !list.isNullOrEmpty() && isSelectionEnabled){

            val selectList: MutableList<AdapterItem> = mutableListOf()
            for (item in list){
                selectList.add(AdapterItem(item.id, item.firstText, item.secondText))
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

        longClickableListItemViewAdapter = LongClickableListItemViewAdapter(getAdapterItemList(), this)
        recyclerView?.adapter = longClickableListItemViewAdapter
        if(searchQuery!=null){
            longClickableListItemViewAdapter?.filter(searchQuery)
        }
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    override fun selectionCount(size: Int) {
        setFragmentResult("FragmentSelectionCount", bundleOf("selected_count" to size))
    }

    override fun switchToolbar(shouldSwitchToolbar: Boolean) {
        setFragmentResult("FragmentSwitchToolbar", bundleOf("switch_toolbar" to shouldSwitchToolbar))
    }

    override fun onDelete(id: List<Int>) {
        val deleteFragment = CollegeDeleteDialog()
        deleteFragment.setCollegeID(id[0])
        deleteFragment.show(requireActivity().supportFragmentManager, "deleteDialog")
    }

    override fun onUpdate(id: List<Int>) {
        val editFragment = CollegeUpdateBottomSheet.newInstance(id[0])
        editFragment?.show(requireActivity().supportFragmentManager, "updateBottomSheetDialog")
    }

    override fun onClick(id: List<Int>) {
        val intent = Intent(requireContext(), CollegeActivity::class.java)
        editCollegeId = id[0]
        intent.putExtras(
            Bundle().apply {
                putInt("collegeID", id[0])
            })
        startActivity(intent)
    }

    override fun onLongClick(id: List<Int>) {
        isSelectionEnabled = true
        val selectList: MutableList<AdapterItem> = mutableListOf()
        val list = longClickableListItemViewAdapter?.filter(searchQuery)
        for (item in list ?: return){
            selectList.add(AdapterItem(item.id, item.firstText, item.secondText))
        }
        selectableListItemViewAdapter = SelectableListItemViewAdapter(mutableListOf(id),selectList, this , collegeDAO)
        recyclerView?.adapter = selectableListItemViewAdapter
        switchToolbar(true)
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
            longClickableListItemViewAdapter?.updateItem(
                AdapterItem(
                    listOf(editCollegeId!!),
                    getString(R.string.college_id, editCollegeId),
                    "${collegeDAO.get(editCollegeId)?.name}"
                )
            )
            editCollegeId=null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val adapter = selectableListItemViewAdapter
        outState.putBoolean("is_selection_enabled", isSelectionEnabled)
        outState.putString("search_query", searchQuery)
        if(adapter!=null){
            val idStringList = adapter.selectedItemsIds.map { it.joinToString ("-") }.toTypedArray()
            outState.putStringArray("selected_items_id_string_list", idStringList)
        }
    }

    private fun getAdapterItemList(): MutableList<AdapterItem>{
        val list = collegeDAO.getList()
        return list.map {
            AdapterItem(
                listOf(it.id),
                getString(R.string.college_id, it.id),
                it.name
            )
        }.toMutableList()
    }
}

