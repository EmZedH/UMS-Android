package com.example.ums.bottomsheetdialogs

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.model.Transactions
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TransactionDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class TransactionAddBottomSheet private constructor(): FullScreenBottomSheetDialog() {

    companion object{
        fun newInstance(studentID: Int?): TransactionAddBottomSheet?{
            val bottomSheet = TransactionAddBottomSheet()
            bottomSheet.arguments = Bundle().apply {
                putInt("student_id", studentID ?: return null)
            }
            return bottomSheet
        }
    }

    private lateinit var transactionAmount: TextInputLayout
    private lateinit var transactionDate: TextInputLayout
    
    private var studentID: Int? = null

    private lateinit var transactionAmountText: String
    private lateinit var transactionDateText: String
    
    private var transactionAmountError: String? = null
    private var transactionDateError: String? = null

    private var savedDate: Int? = null
    private var savedMonth: Int? = null
    private var savedYear: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        studentID = arguments?.getInt("student_id")
        
        transactionAmountText = savedInstanceState?.getString("transaction_id_contact_number_text") ?: ""
        transactionDateText = savedInstanceState?.getString("transaction_id_dob_text") ?: ""

        savedYear = savedInstanceState?.getInt("transaction_id_birth_year")
        savedMonth = savedInstanceState?.getInt("transaction_id_birth_month")
        savedDate = savedInstanceState?.getInt("transaction_id_birth_date")

        transactionAmountError = savedInstanceState?.getString("transaction_id_contact_number_error")
        transactionDateError = savedInstanceState?.getString("transaction_id_date_of_birth_error")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_transaction, container, false)
        val transactionDAO = TransactionDAO(DatabaseHelper(requireActivity()))
        val addStudentButton = view.findViewById<MaterialButton>(R.id.add_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)

        transactionAmount = view.findViewById(R.id.transaction_amount)
        transactionDate = view.findViewById(R.id.date_of_transaction)


        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        if(transactionAmountText.isNotEmpty()){
            transactionAmount.editText?.setText(transactionAmountText)
        }
        if(transactionDateText.isNotEmpty()){
            transactionDate.editText?.setText(transactionDateText)
        }

        setCollegeIDTextView(view)

        dateEntryTextInputLayout(transactionDate)
        transactionAmount.editText?.addTextChangedListener(textListener(transactionAmount) {
            transactionAmountError = null
        })
        transactionDate.editText?.addTextChangedListener(textListener(transactionDate) {
            transactionDateError = null
        })

        addStudentButton.setOnClickListener {

            var flag = true

            transactionAmountText = transactionAmount.editText?.text.toString()
            transactionDateText = transactionDate.editText?.text.toString()

            if (transactionAmountText.isEmpty()) {
                flag = false
                transactionAmountError = "Don't leave contact number field blank"
                transactionAmount.error = transactionAmountError
            }
            else if(transactionAmountText.toInt() < 100){
                flag = false
                transactionAmountError = "Please enter amount above 100"
                transactionAmount.error = transactionAmountError
            }
            if (transactionDateText.isEmpty()) {
                flag = false
                transactionDateError = "Don't leave date of birth field blank"
                transactionDate.error = transactionDateError
            }
            else if(!Utility.isCorrectDateFormat(transactionDateText)){
                flag = false
                transactionDateError = "Please enter proper date format (yyyy-dd-mm)"
                transactionDate.error = transactionDateError
            }

            if (flag) {
                val newID = transactionDAO.getNewID()
                val studentDAO = StudentDAO(DatabaseHelper(requireActivity()))
                val semester = studentDAO.get(studentID)?.semester
                if(studentID!=null){
                    transactionDAO.insert(
                        Transactions(
                            newID,
                            studentID ?: return@setOnClickListener,
                            semester ?: return@setOnClickListener,
                            transactionDateText,
                            transactionAmountText.toInt()
                        )
                    )
                }

                setCollegeIDTextView(view)
                setFragmentResult("TransactionAddFragmentPosition", bundleOf("id" to newID))
                dismiss()
            }
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        clearErrors()
    }


    private fun clearErrors(){
        transactionAmount.error = null
        transactionDate.error = null
    }

    private fun setCollegeIDTextView(view : View){
        val transactionDAO = TransactionDAO(DatabaseHelper(requireActivity()))
        view.findViewById<TextView>(R.id.id_text_view)?.setText(R.string.id_string)
        view.findViewById<TextView>(R.id.id_text_view)?.append(" T/${transactionDAO.getNewID()}")
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val calendarYear = calendar.get(Calendar.YEAR)
        val calendarMonth = calendar.get(Calendar.MONTH)
        val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, date->
            val selectedDate = "${year}-${date}-${month + 1}"
            transactionDate.editText?.setText(selectedDate)
            if(year!=0 && month!=0 && date!=0){
                savedDate = date
                savedMonth = month
                savedYear = year
            }
        }, savedYear ?: calendarYear, savedMonth ?: calendarMonth, savedDate ?: calendarDay)

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("transaction_id_contact_number_text", transactionAmount.editText?.text.toString())
        outState.putString("transaction_id_dob_text", transactionDate.editText?.text.toString())
        outState.putString("transaction_id_contact_number_error", transactionAmountError)
        outState.putString("transaction_id_date_of_birth_error", transactionDateError)
        transactionAmount.error = transactionAmountError
        transactionDate.error = transactionDateError

        savedDate?.let { savedDate -> outState.putInt("transaction_id_birth_date", savedDate) }
        savedMonth?.let { savedMonth -> outState.putInt("transaction_id_birth_month", savedMonth) }
        savedYear?.let { savedYear -> outState.putInt("transaction_id_birth_year", savedYear) }
    }


    private fun textListener(layout: TextInputLayout, errorOperation: (() -> Unit)): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.error = null
                errorOperation()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }

    private fun dateEntryTextInputLayout(textInputLayout: TextInputLayout?){
        textInputLayout?.editText?.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                showDatePicker()
            }
        }
        textInputLayout?.editText?.setOnClickListener {
            showDatePicker()
        }
        textInputLayout?.setStartIconOnClickListener {
            showDatePicker()
        }
    }
}