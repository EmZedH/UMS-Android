package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ums.CollegeListItemViewAdapter
import com.example.ums.listener.CollegeAddListener

class SuperAdminSharedViewModel: ViewModel() {
    private val _listener = MutableLiveData<CollegeAddListener>()
    private val _collegeID = MutableLiveData<Int>()
    private val _adapter = MutableLiveData<CollegeListItemViewAdapter>()

    fun getListener(): MutableLiveData<CollegeAddListener> {
        return _listener
    }
    fun setListener(listener: CollegeAddListener){
        _listener.value = listener
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