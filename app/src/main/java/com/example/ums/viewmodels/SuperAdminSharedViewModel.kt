package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ums.adapters.CollegeListItemViewAdapter
import com.example.ums.listener.AddListener

class SuperAdminSharedViewModel: ViewModel() {
    private val _addListener = MutableLiveData<AddListener>()
    private val _collegeAdapter = MutableLiveData<CollegeListItemViewAdapter>()

    fun getAddListener() = _addListener
    fun setAddListener(listener: AddListener){
        _addListener.value = listener
    }
    fun getAdapter() = _collegeAdapter
    fun setAdapter(adapter: CollegeListItemViewAdapter){
        _collegeAdapter.value = adapter
    }
}