package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ums.listener.AddCollegeListener

class AddCollegeBottomSheetViewModel: ViewModel() {

    private val _addCollegeListener = MutableLiveData<AddCollegeListener>()
    private val _collegeNameText = MutableLiveData<String>()
    private val _collegeAddressText = MutableLiveData<String>()
    private val _collegeTelephoneText = MutableLiveData<String>()

    fun getListener() = _addCollegeListener
    fun setListener(adapter: AddCollegeListener){
        _addCollegeListener.value = adapter
    }
    fun getCollegeName() = _collegeNameText.value
    fun setCollegeName(text: String){
        _collegeNameText.value = text
    }
    fun getCollegeAddress() = _collegeAddressText.value
    fun setCollegeAddress(text: String){
        _collegeAddressText.value = text
    }
    fun getCollegeTelephone() = _collegeTelephoneText.value
    fun setCollegeTelephone(text: String){
        _collegeTelephoneText.value = text
    }
}