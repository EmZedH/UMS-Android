package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.interfaces.DeletableDAO
import com.example.ums.interfaces.SelectionListener
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.model.SelectionItem

class SelectableListItemViewAdapter(var selectedItemsIds: MutableList<String>, var originalList: MutableList<SelectionItem>, private val listener: SelectionListener, private val deletableDAO: DeletableDAO) : RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var shouldSelectAll = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val item = originalList[position]

        holder.firstTextView.text = item.firstText
        holder.secondTextView.text = item.secondText

        if(shouldSelectAll){
            selectedItemsIds = originalList.map { it.idString }.toMutableList()
            setSelectedItemView(holder)
        }
        if(selectedItemsIds.containsAll(originalList.map{ it.idString })){
            shouldSelectAll = false
        }
        if(selectedItemsIds.contains(item.idString)){
            setSelectedItemView(holder)
        }
        holder.itemView.setOnClickListener {
            if(selectedItemsIds.contains(item.idString)){
                selectedItemsIds.remove(item.idString)
                setUnSelectedItemView(holder)
            }
            else{
                selectedItemsIds.add(item.idString)
                setSelectedItemView(holder)
            }
            listener.selectionCount(selectedItemsIds.size)
            if(selectedItemsIds.isEmpty()){
                listener.switchToolbar(false)
            }
        }
    }

    fun deleteAll(){
        deletableDAO.deleteList(selectedItemsIds)
        listener.clearSelection()
    }

    fun selectAll(){
        shouldSelectAll = true
        notifyDataSetChanged()
    }

    private fun setSelectedItemView(holder: ClickableListItemViewHolder){
        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_inversePrimary))
    }

    private fun setUnSelectedItemView(holder: ClickableListItemViewHolder){
        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_surface))
    }
}