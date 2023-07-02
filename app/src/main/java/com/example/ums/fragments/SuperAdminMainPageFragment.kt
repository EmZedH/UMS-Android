package com.example.ums.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.CollegeActivity
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.adapters.CollegeListItemViewAdapter
import com.example.ums.bottomsheetdialogs.CollegeAddBottomSheet
import com.example.ums.bottomsheetdialogs.CollegeUpdateBottomSheet
import com.example.ums.dialogFragments.CollegeDeleteDialog
import com.example.ums.listener.ItemListener
import com.example.ums.model.databaseAccessObject.CollegeDAO

class SuperAdminMainPageFragment : AddableSearchableFragment(), ItemListener {

    private lateinit var addCollegeBottomSheet : CollegeAddBottomSheet
    private var collegeListItemViewAdapter : CollegeListItemViewAdapter? = null
    private lateinit var collegeDAO: CollegeDAO
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var editCollegeId: Int? = null

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

        val view = inflater.inflate(R.layout.fragment_list_page, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_view)

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
        recyclerView.adapter = collegeListItemViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
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
        collegeListItemViewAdapter?.filter(query)
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
}

