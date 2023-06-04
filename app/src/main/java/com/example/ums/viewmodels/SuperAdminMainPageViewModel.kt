package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ums.CollegeListItemViewAdapter

class SuperAdminMainPageViewModel: ViewModel() {
    private val _collegeListItemViewAdapter = MutableLiveData<CollegeListItemViewAdapter>()

    fun getAdapter() = _collegeListItemViewAdapter
    fun setAdapter(adapter: CollegeListItemViewAdapter){
        _collegeListItemViewAdapter.value = adapter
    }
}