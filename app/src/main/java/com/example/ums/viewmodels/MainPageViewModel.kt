package com.example.ums.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainPageViewModel: ViewModel() {
    private val _isSearchViewOpen = MutableLiveData<Boolean?>()
    private val _query = MutableLiveData<String>()

    val isSearchViewOpen = _isSearchViewOpen
    val query = _query

    fun setSearchView(isSearchViewOpen: Boolean?){
        _isSearchViewOpen.value = isSearchViewOpen
    }

    fun setQuery(query: String){
        _query.value = query
    }

}