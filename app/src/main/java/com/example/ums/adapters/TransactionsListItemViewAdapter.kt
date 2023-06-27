package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DeletableListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.Transactions
import com.example.ums.model.databaseAccessObject.TransactionDAO

class TransactionsListItemViewAdapter (private val studentID: Int, private val transactionDAO: TransactionDAO, private val itemListener: ItemListener): RecyclerView.Adapter<DeletableListItemViewHolder>() {

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
        holder.secondTextView.text = "Semester 1"
        holder.itemView.setOnClickListener {
//            val bundle = Bundle().apply {
//                putInt("professor_profile_department_id", departmentID)
//                putInt("professor_profile_college_id", collegeID)
//                putInt("professor_profile_professor_id", transactions.user.id)
//            }
//            itemListener.onClick(bundle)
        }
        holder.deleteButton.setOnClickListener {
            itemListener.onDelete(transactions.id)
        }
//        holder.optionsButton.setOnClickListener {
//            showOptionsPopupMenu(transactions, holder)
//        }
    }

//    private fun showOptionsPopupMenu(transactions : Transactions, holder: ListItemViewHolder){
//        val context = holder.itemView.context
//        val popupMenu = PopupMenu(context, holder.optionsButton)
//
//        popupMenu.inflate(R.menu.edit_delete_menu)
//
//        popupMenu.setOnMenuItemClickListener{menuItem ->
//            when (menuItem.itemId) {
//                R.id.edit_college -> {
//                    itemListener.onUpdate(transactions.id)
//                    true
//                }
//                R.id.delete_college -> {
//                    itemListener.onDelete(transactions.id)
//                    true
//                }
//
//                else -> {
//                    false
//                }
//            }}
//        popupMenu.show()
//    }

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

//    fun updateItemInAdapter(id: Int){
//        val query = filterQuery
//        val transaction = transactionDAO.get(id) ?: return
//        for (listProfessor in originalList){
//            if(listProfessor.user.id == transaction.user.id){
//                if(query!=null && query!=""){
//                    val flag = transaction.user.name.lowercase().contains(query.lowercase())
//                    if(flag){
//                        originalList.apply {
//                            set(originalList.indexOf(listProfessor), transaction)
//                            sortBy { it.user.id }
//                            notifyItemChanged(originalList.indexOf(transaction))
//                        }
//                        return
//                    }
//                    else{
//                        originalList.apply {
//                            notifyItemRemoved(originalList.indexOf(listProfessor))
//                            remove(listProfessor)
//                            sortBy { it.user.id }
//                        }
//                        return
//                    }
//                }
//                else{
//                    originalList.apply {
//                        set(originalList.indexOf(listProfessor), transaction)
//                        sortBy { it.user.id }
//                        notifyItemChanged(originalList.indexOf(transaction))
//                    }
//                    return
//                }
//            }
//        }
//    }

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