package com.example.ums.listener

interface OpenCourseItemListener: ClickListener {
    fun onDelete(courseID: Int, departmentID: Int)
}