package com.ramadhan.couriertracking.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramadhan.couriertracking.R
import com.ramadhan.couriertracking.data.entity.History
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter(private val histories: MutableList<History>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false),
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val item = histories[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    fun getData(position: Int): History = histories[position]

    fun addList(list: List<History>) {
        histories.clear()
        histories.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class ViewHolder(itemView: View, private val itemClick: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(history: History) {
            itemView.historyTitle.text = history.title ?: history.awb
            itemView.historyAWB.text = history.awb
            itemView.historyCourier.text = history.courier.name
        }

        override fun onClick(v: View?) {
            itemClick?.onItemClick(v, adapterPosition)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}