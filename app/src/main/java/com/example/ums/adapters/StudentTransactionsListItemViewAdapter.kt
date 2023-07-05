package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.superAdminCollegeAdminActivities.StudentTransactionsListItemViewHolder
import com.example.ums.model.Transactions
import com.example.ums.model.databaseAccessObject.TransactionDAO

class StudentTransactionsListItemViewAdapter (private val studentID: Int, private val transactionDAO: TransactionDAO): RecyclerView.Adapter<StudentTransactionsListItemViewHolder>() {

    private var originalList : MutableList<Transactions> = transactionDAO.getList(studentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentTransactionsListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.student_transaction_list_item_layout, parent, false)
        return StudentTransactionsListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: StudentTransactionsListItemViewHolder, position: Int) {
        val transactions = originalList[position]
        holder.firstTextView.text = "ID: T/${transactions.id}"
        holder.amountTextView.text = "₹ ${transactions.amount}"
        holder.secondTextView.text = "Semester ${transactions.semester}"
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                transactionDAO.getList(studentID).sortedBy { it.id }
            else
                transactionDAO.getList(studentID).filter { transaction -> transaction.semester.toString().contains(query, ignoreCase = true) }.sortedBy { it.id }

        filterQuery = if(query.isNullOrEmpty()) null else query

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun addItem(id: Int){
        val query = filterQuery
        val transaction = transactionDAO.get(id)
        if(transaction!=null){
            if(query!=null && transaction.semester.toString().lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(transaction)
                    sortBy {transaction -> transaction.id }
                    notifyItemInserted(indexOf(transaction))
                }
            }
            else if(query==null){
                originalList.add(transaction)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun updateList(){
        originalList = transactionDAO.getList(studentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}