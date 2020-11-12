package com.inbedroom.couriertracking.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.data.entity.Tracking
import kotlinx.android.synthetic.main.item_tracking_info.view.*

class TrackingRecyclerViewAdapter(
    private val context: Context,
    private var trackingList: MutableList<Tracking>
) : RecyclerView.Adapter<TrackingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackingRecyclerViewAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_tracking_info, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrackingRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = trackingList[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return trackingList.size
    }

    fun updateItem(tracking: List<Tracking>){
        trackingList.clear()
        trackingList.addAll(tracking)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(tracking: Tracking) {
            itemView.itemInfoDate.text = tracking.date.subSequence(0, 10)
            itemView.itemInfoTime.text = tracking.date.drop(10)
            if (tracking.location.isNullOrEmpty()){
                itemView.itemInfoDetail.text = tracking.desc
            }else{
                itemView.itemInfoDetail.text = itemView.context.getString(R.string.track_desc, tracking.desc, tracking.location)
            }
        }
    }
}