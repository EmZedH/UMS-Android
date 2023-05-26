package com.example.ums

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangePasswordBottomSheet(private val userID : Int, private val userDAO: UserDAO) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        val closeButton = view.findViewById<ImageButton>(R.id.change_password_close_buutton)

        closeButton.setOnClickListener{
            dismiss()
        }

        return view
    }
}