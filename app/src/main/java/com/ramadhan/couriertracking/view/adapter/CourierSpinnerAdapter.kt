package com.ramadhan.couriertracking.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.ramadhan.couriertracking.R
import com.ramadhan.couriertracking.data.entity.Courier
import kotlinx.android.synthetic.main.item_courier.view.*

class CourierSpinnerAdapter(context: Context, dataSource: List<Courier>) :
    ArrayAdapter<Courier>(context, 0, dataSource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.createView(position, convertView, parent)
    }

    @Suppress("DEPRECATION")
    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val courier = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_courier, parent, false)

        val imgId =
            context.resources.getIdentifier(courier?.imgUrl, "drawable", context.packageName)


        if (courier != null) {
            view.courierItemName.text = courier.name
            Glide.with(view).load(imgId).into(view.courierItemIcon)
        }

        return view
    }
}