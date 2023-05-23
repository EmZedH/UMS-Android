package com.example.ums.model

data class Course (
    var courseID : Int,
    var courseName : String,
    var courseSemester : Int,
    var courseDegree : String,
    var departmentID : Int,
    var collegeID : Int,
    var courseElective : String
    )