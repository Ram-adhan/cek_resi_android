package com.ramadhan.couriertracking.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ramadhan.couriertracking.R
import com.ramadhan.couriertracking.data.response.entity.Courier
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

        val imgId = context.resources.getIdentifier(courier?.imgUrl, "drawable", context.packageName)


        if (courier != null) {
            view.courierItemName.text = courier.name
            view.courierItemIcon.setImageResource(imgId)
            if(!courier.available){
                val bgColor: Int
                val txtColor: Int
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    bgColor = context.getColor(R.color.colorAccent)
                    txtColor = context.getColor(R.color.red)

                }else{
                    bgColor = parent.resources.getColor(R.color.colorAccent)
                    txtColor = parent.resources.getColor(R.color.red)
                }
                view.setBackgroundColor(bgColor)
                view.courierItemName.setTextColor(txtColor)
            }
        }

        return view
    }
}