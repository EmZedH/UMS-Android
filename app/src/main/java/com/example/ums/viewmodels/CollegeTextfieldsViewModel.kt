package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CollegeTextfieldsViewModel: ViewModel() {

    private val _collegeNameText = MutableLiveData<String?>()
    private val _collegeAddressText = MutableLiveData<String?>()
    private val _collegeTelephoneText = MutableLiveData<String?>()
    fun getCollegeName() = _collegeNameText.value
    fun setCollegeName(text: String?){
        _collegeNameText.value = text
    }
    fun getCollegeAddress() = _collegeAddressText.value
    fun setCollegeAddress(text: String?){
        _collegeAddressText.value = text
    }
    fun getCollegeTelephone() = _collegeTelephoneText.value
    fun setCollegeTelephone(text: String?){
        _collegeTelephoneText.value = text
    }
}