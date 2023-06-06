package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangePasswordBottomSheetViewModel: ViewModel() {
    private val _userID = MutableLiveData<Int>()
    private val _currentPassword = MutableLiveData<String>()
    private val _newPassword = MutableLiveData<String>()
    private val _confirmPassword = MutableLiveData<String>()

    fun getUserID() = _userID
    fun getCurrentPassword() = _currentPassword
    fun getNewPassword() = _newPassword
    fun getConfirmPassword() = _confirmPassword

    fun setUserID(userID: Int){
        _userID.value = userID
    }
    fun setCurrentPassword(password: String){
        _currentPassword.value = password
    }
    fun setNewPassword(password: String){
        _newPassword.value = password
    }
    fun setConfirmPassword(password: String){
        _confirmPassword.value = password
    }
}