package com.android.statussavvy.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

public class RvUpdater<T : Any?>(
    private val oldList: MutableList<T>,
    private val newList: MutableList<T>,
    private val areItemsTheSame: (oldData: T, newData: T) -> Boolean,
    private val areContentsTheSame: (oldData: T, newData: T) -> Boolean
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
       return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }
    fun updateList(adapter:RecyclerView.Adapter<*>)= with(DiffUtil.calculateDiff(this)){
        oldList.clear()
        oldList.addAll(newList)
        dispatchUpdatesTo(adapter)
    }
}