package com.android.statussavvy.base


import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.statussavvy.utils.RvUpdater

public abstract class BaseRvAdapter<T : Any, VH : BaseRvViewHolder>(context: Context, var list: List<T>) :
    RecyclerView.Adapter<VH>() {
    protected var onItemClickListener: OnItemClickListener? = null
    protected var onItemLongClickListener: OnItemLongClickListener? = null
    protected var position = -1


    /*private var list_ = mutableListOf<T>()
        set(value) {
            field=value.toMutableList()
        }
*/
    override fun onBindViewHolder(holder: VH, position: Int) {
        //item click listener
        this.position = position
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener { v ->
                onItemClickListener?.onItemClick(v, position)
            }
        }

        /* if (onItemLongClickListener != null) {
             holder.itemView.setOnLongClickListener { v ->
                //dd
             }
         }*/

        onBindData(holder, list[position])
    }

    /* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
         var v:View=LayoutInflater.from(parent.context).inflate(R.layout.activity_main,parent,false)
         return VH(v)
     }*/

    // abstract fun setLayoutItem(): Int
    protected abstract fun onBindData(holder: VH, t: T)

    override fun getItemCount(): Int {
        return if (list == null) 0 else list.size
    }


    open fun addItem(data: T) {
        list.toMutableList().add(data)
        notifyDataSetChanged()
    }

    open fun addItemToPosition(data: T, position: Int) {
        list.toMutableList().add(position, data)
        notifyItemInserted(position)
    }


    open fun addAllItems(data: List<T>) {
        val oldCount = list.size
        list.toMutableList().addAll(data)
        notifyItemRangeInserted(oldCount, data.size)
    }

    open fun removeItem(position: Int) {
        list.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }

    open fun clearAllItems() {
        list.toMutableList().clear()
    }

    public interface OnItemClickListener {
        public fun onItemClick(v: View?, position: Int)
    }

    public interface OnItemLongClickListener {
        public fun onItemLongClick(v: View?, position: Int)
    }

    @JvmName("setOnItemClickListener1")
    public fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    @JvmName("setOnItemLongClickListener1")
    public fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener
    }

   /* public fun setData(new_list:List<T>){
     val diffUtil=RvUpdater(list.toMutableList(),new_list.toMutableList(),a,"")
    }*/
}