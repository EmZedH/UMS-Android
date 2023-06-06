package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ums.CollegeListItemViewAdapter
import com.example.ums.listener.AddListener

class SuperAdminSharedViewModel: ViewModel() {
    private val _addListener = MutableLiveData<AddListener>()
    private val _collegeID = MutableLiveData<Int>()
    private val _adapter = MutableLiveData<CollegeListItemViewAdapter>()

    fun getAddListener() = _addListener
    fun setAddListener(listener: AddListener){
        _addListener.value = listener
    }

    fun getID() = _collegeID
    fun setID(id: Int){
        _collegeID.value = id
    }
    fun getAdapter() = _adapter
    fun setAdapter(adapter: CollegeListItemViewAdapter){
        _adapter.value = adapter
    }
}