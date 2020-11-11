package com.inbedroom.couriertracking.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.platform.AdapterItem
import com.inbedroom.couriertracking.data.entity.Ongkir
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_ongkir_detail.view.*

class OngkirDetailAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: MutableList<AdapterItem<Ongkir>> = mutableListOf()
    private val itemList = mutableListOf<Ongkir>()

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(item: Ongkir) {

            if (!item.service.isNullOrEmpty()){
                val etd = itemView.context.getString(R.string.etd, item.etd)
                itemView.serviceDetail.text =
                    itemView.context.getString(R.string.service_detail, item.service, etd)
                itemView.serviceCourier.text = item.courier
                itemView.serviceCost.text = itemView.context.getString(R.string.price, item.cost)
            }else{
                itemView.serviceDetail.text = itemView.context.getString(R.string.no_service)
                itemView.serviceCourier.invisible()
                itemView.serviceCost.invisible()
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(title: String) {
            itemView.adapterHeaderTitle.text = title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(data[position]){
            is AdapterItem.Item -> TYPE_ITEM
            else -> TYPE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ITEM) {
            ItemViewHolder(
                inflater.inflate(R.layout.item_ongkir_detail, parent, false)
            )
        } else {
            HeaderViewHolder(
                inflater.inflate(R.layout.item_header, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val adapterItem = data[position]) {
            is AdapterItem.Item -> {
                val itemHolder = holder as ItemViewHolder
                adapterItem.value?.let { itemHolder.bindItem(it) }
            }
            is AdapterItem.Header -> {
                val itemHolder = holder as HeaderViewHolder
                adapterItem.title.let { itemHolder.bindItem(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: List<Ongkir>) {
        itemList.clear()
        itemList.addAll(newData)
        processHeader()
        notifyDataSetChanged()
    }

    private fun processHeader() {
        var prevTitle = ""
        data.clear()
        itemList.forEach { value ->
            val currentTitle = value.courier
            if (prevTitle != currentTitle) {
                data.add(AdapterItem.Header(currentTitle))
                prevTitle = currentTitle
            }
            data.add(AdapterItem.Item(value))
        }
    }

}