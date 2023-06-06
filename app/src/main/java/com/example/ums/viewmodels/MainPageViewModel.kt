package com.example.ums.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainPageViewModel: ViewModel() {
    private val _isSearchViewOpen = MutableLiveData<Boolean>()
    private val _query = MutableLiveData<String>()

    var isSearchViewOpen: LiveData<Boolean> = _isSearchViewOpen
    var query: LiveData<String> = _query

}