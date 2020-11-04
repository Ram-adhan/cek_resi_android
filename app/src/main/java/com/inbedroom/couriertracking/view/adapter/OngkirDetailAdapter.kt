package com.inbedroom.couriertracking.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.data.entity.Ongkir
import kotlinx.android.synthetic.main.item_ongkir_detail.view.*

class OngkirDetailAdapter(private val data: MutableList<Ongkir>) :
    RecyclerView.Adapter<OngkirDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(item: Ongkir){
            itemView.serviceDetail.text = itemView.context.getString(R.string.service_detail, item.service, item.etd)
            itemView.serviceCourier.text = item.courier
            itemView.serviceCost.text = item.cost
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ongkir_detail, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun replaceData(newData: List<Ongkir>){
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: Ongkir){
        data.add(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: List<Ongkir>){
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun removeData(){
        data.clear()
        notifyDataSetChanged()
    }
}