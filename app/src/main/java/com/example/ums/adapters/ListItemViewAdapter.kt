package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.interfaces.ListIdItemListener
import com.example.ums.listItemViewHolder.ListItemViewHolder
import com.example.ums.model.AdapterItem

class ListItemViewAdapter(list: MutableList<AdapterItem>, private val listener: ListIdItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var list = list.sortedBy { it.id[0] }.toMutableList()
    private var preFilteredList = list.sortedBy { it.id[0] }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = list[position]
        holder.itemIDTextView.text = item.firstText
        holder.itemNameTextView.text = item.secondText
        holder.itemView.setOnLongClickListener {
            listener.onLongClick(item.id)
            true
        }
        holder.itemView.setOnClickListener {
            listener.onClick(item.id)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(item, holder)
        }
    }

    private fun showOptionsPopupMenu(item : AdapterItem, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    listener.onUpdate(item.id)
                    true
                }
                R.id.delete_college -> {
                    listener.onDelete(item.id)
                    true
                }

                else -> {
                    false
                }
            }}
        popupMenu.show()
    }

    fun filter(query: String?): List<AdapterItem>{
        val filteredList =
            if(query.isNullOrEmpty())
                preFilteredList.sortedBy { it.id[0] }
            else
                preFilteredList.filter { it.secondText.contains(query, true) }.sortedBy { it.id[0] }

        filterQuery = if(query.isNullOrEmpty()) null else query
        list.clear()
        list.addAll(filteredList)
        notifyDataSetChanged()
        return filteredList
    }

    fun updateItemInAdapter(adapterItem: AdapterItem) {
        val query = filterQuery
        if(list.map { it.id }.contains(adapterItem.id)){
            val index = list.map { it.id }.indexOf(adapterItem.id)
            if(!query.isNullOrEmpty()){
                val flag = adapterItem.secondText.lowercase().contains(query.lowercase())
                if(flag){
                    list[index] = adapterItem
                    notifyItemChanged(list.indexOf(adapterItem))
                }
                else{
                    list.removeAt(index)
                    notifyItemRemoved(index)
                }
            }
            else{
                list[list.map { it.id }.indexOf(adapterItem.id)] = adapterItem
                notifyItemChanged(list.indexOf(adapterItem))
            }
        }
        val idList = preFilteredList.map { it.id }
        if(idList.contains(adapterItem.id)){
            preFilteredList[idList.indexOf(adapterItem.id)] = adapterItem
        }
    }

    fun addItem(adapterItem: AdapterItem){

        val query= filterQuery
        if(query==null){
            list.add(adapterItem)
            list.sortBy { it.id[0] }
            notifyItemInserted(list.indexOf(adapterItem))
        }
        else if(adapterItem.secondText.lowercase().contains(query.lowercase())){
            list.add(adapterItem)
            list.sortBy { it.id[0] }
            notifyItemInserted(list.indexOf(adapterItem))
        }
        preFilteredList.add(adapterItem)
        preFilteredList.sortBy { it.id[0] }
    }

    fun deleteItem(id: List<Int>){
        val index = list.map { it.id }.indexOf(id)
        list.removeAt(index)
        preFilteredList.removeAt(preFilteredList.map { it.id }.indexOf(id))
        notifyItemRemoved(index)
    }

    fun updateList(list: MutableList<AdapterItem>){
        this.list = list
        preFilteredList = list
        filterQuery = null
        notifyDataSetChanged()
    }
}