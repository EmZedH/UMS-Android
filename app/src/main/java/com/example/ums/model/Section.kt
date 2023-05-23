package com.example.ums.model

data class Section (
    var sectionID : Int,
    var sectionName : String,
    val departmentID : Int,
    val collegeID : Int
    )