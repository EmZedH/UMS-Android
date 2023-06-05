package com.example.ums.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ums.CollegeListItemViewAdapter
import com.example.ums.listener.AddCollegeListener

class SuperAdminMainPageViewModel: ViewModel() {
    private val _listener = MutableLiveData<AddCollegeListener>()

    fun getListener(): MutableLiveData<AddCollegeListener> {
        Log.i("ViewModelSuperAdminMainPage","get: ${_listener.value}")
        return _listener
    }
    fun setListener(listener: AddCollegeListener){
        _listener.value = listener
        Log.i("ViewModelSuperAdminMainPage","set: ${_listener.value}")
    }
}