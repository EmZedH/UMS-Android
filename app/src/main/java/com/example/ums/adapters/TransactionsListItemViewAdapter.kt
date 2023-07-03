package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.DeletableListItemViewHolder
import com.example.ums.listener.DeleteListener
import com.example.ums.model.Transactions
import com.example.ums.model.databaseAccessObject.TransactionDAO

class TransactionsListItemViewAdapter (private val studentID: Int, private val transactionDAO: TransactionDAO, private val itemListener: DeleteListener): RecyclerView.Adapter<DeletableListItemViewHolder>() {

    private var originalList : MutableList<Transactions> = transactionDAO.getList(studentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.deletable_list_item_layout, parent, false)
        return DeletableListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: DeletableListItemViewHolder, position: Int) {
        val transactions = originalList[position]
        holder.firstTextView.text = "ID: T/${transactions.id}"
        holder.secondTextView.text = "₹${transactions.amount} (Semester ${transactions.semester})"
        holder.deleteButton.setOnClickListener {
            itemListener.onDelete(transactions.id)
        }
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                transactionDAO.getList(studentID).sortedBy { it.id }
            else
                transactionDAO.getList(studentID).filter { transaction -> "₹${transaction.amount} (Semester ${transaction.semester})".contains(query, ignoreCase = true) }.sortedBy { it.id }

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

    fun deleteItem(id: Int){
        val professor = transactionDAO.get(id)
        val updatedPosition = originalList.indexOf(professor)
        transactionDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun updateList(){
        originalList = transactionDAO.getList(studentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}