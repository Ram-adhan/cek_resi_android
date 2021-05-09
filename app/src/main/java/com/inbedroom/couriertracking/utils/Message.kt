package com.inbedroom.couriertracking.utils

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.customview.DialogEditTitle

object Message {
    fun customDialog(context: Context, msg: String, title: String, positiveAction: DialogInterface.OnClickListener?) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(msg)

        dialogBuilder.setPositiveButton(context.getString(R.string.ok_button), positiveAction)

        dialogBuilder.show()
    }

    fun alert(context: Context, msg: String, positiveAction: DialogInterface.OnClickListener?) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(context.getString(R.string.alert_title))
        dialogBuilder.setMessage(msg)

        dialogBuilder.setPositiveButton(context.getString(R.string.ok_button), positiveAction)

        dialogBuilder.show()
    }

    fun alertEditText(
        fragmentManager: FragmentManager,
        positiveButton: DialogEditTitle.DialogListener
    ) {
        val dialog = DialogEditTitle(positiveButton)
        val fragmentTransaction = fragmentManager.beginTransaction()
        val prevDialog = fragmentManager.findFragmentByTag("dialog")
        if (prevDialog != null) {
            fragmentTransaction.remove(prevDialog)
        }
        fragmentTransaction.addToBackStack(null)
        dialog.show(fragmentTransaction, "dialog")
    }

    fun notify(view: View, msg: String, anchorView: View? = null) {
        val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        if (anchorView != null){
            snackBar.anchorView = anchorView
        }
        snackBar.show()
    }

    fun toast(context: Context, msg: String) {
        val snackBar = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        snackBar.show()
    }
}