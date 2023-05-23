package com.example.ums.model

data class Records (
    var studentID : Int,
    var courseProfessor : CourseProfessor,
    var transactionID : Int,
    var externalMarks : Int,
    var attendance : Int,
    var assignmentMarks : Int,
    var status : String,
    var semCompleted : Int
    )