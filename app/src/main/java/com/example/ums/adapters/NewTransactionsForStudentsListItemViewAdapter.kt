package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ClickListener
import com.example.ums.model.Transactions
import com.example.ums.model.databaseAccessObject.TransactionDAO

class NewTransactionsForStudentsListItemViewAdapter(studentID: Int, private val transactionDAO: TransactionDAO, private val clickListener: ClickListener): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Transactions> = transactionDAO.getCurrentSemesterTransactionList(studentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val transactions = originalList[position]
        holder.firstTextView.text = "ID: T/${transactions.id}"
        holder.secondTextView.text = "Semester ${transactions.semester}"
        holder.itemView.setOnClickListener {

            clickListener.onClick(
                Bundle().apply {
                    putInt("transaction_id", transactions.id)
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return originalList.size
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
                originalList.add(originalList.size, transaction)
                notifyItemInserted(originalList.size)
            }
        }
    }
}