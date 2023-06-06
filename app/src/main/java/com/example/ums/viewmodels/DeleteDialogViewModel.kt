package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeleteDialogViewModel: ViewModel() {
    private val _collegeID = MutableLiveData<Int>()

    fun getID() = _collegeID
    fun setID(id: Int){
        _collegeID.value = id
    }
}