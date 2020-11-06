package com.inbedroom.couriertracking.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.data.entity.Courier
import kotlinx.android.synthetic.main.item_courier.view.*

class CourierSpinnerAdapter(
    context: Context,
    private val dataSource: MutableList<Courier>
) :
    ArrayAdapter<Courier>(context, 0, dataSource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.createView(position, convertView, parent)
    }

    fun addData(newData: List<Courier>) {
        dataSource.clear()
        dataSource.addAll(newData)
        notifyDataSetChanged()
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val courier = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_courier, parent, false)

        if (courier != null) {
            view.courierItemName.text = courier.name
            Glide.with(parent.context).load(courier.imgId).into(view.courierItemIcon)
        }

        return view
    }
}