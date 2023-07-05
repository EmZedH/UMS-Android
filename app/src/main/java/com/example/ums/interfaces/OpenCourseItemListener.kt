package com.example.ums.interfaces

interface OpenCourseItemListener: ClickListener {
    fun onDelete(courseID: Int, departmentID: Int)
}