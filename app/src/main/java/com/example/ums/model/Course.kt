package com.example.ums.model

data class Course (
    var id : Int,
    var name : String,
    var semester : Int,
    var departmentID : Int,
    var collegeID : Int,
    var degree : String,
    var elective : String
    )