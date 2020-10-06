package com.ramadhan.couriertracking.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ramadhan.couriertracking.R
import com.ramadhan.couriertracking.customview.DialogEditTitle
import com.ramadhan.couriertracking.data.entity.Courier
import com.ramadhan.couriertracking.data.entity.History
import com.ramadhan.couriertracking.utils.Injector
import com.ramadhan.couriertracking.view.TrackingDetailActivity.Companion.AWB_NUMBER
import com.ramadhan.couriertracking.view.TrackingDetailActivity.Companion.COURIER_NAME
import com.ramadhan.couriertracking.view.adapter.CourierSpinnerAdapter
import com.ramadhan.couriertracking.view.adapter.HistoryAdapter
import com.ramadhan.couriertracking.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var courierAdapter: CourierSpinnerAdapter
    private lateinit var courierList: List<Courier>
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var spinner: Spinner

    private var courierData: Courier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLib()

        setupUI()

        onAction()
    }

    private fun setupUI() {
        courierList = readFromAsset()
        courierAdapter = CourierSpinnerAdapter(this, courierList)
        mainCourierList.adapter = courierAdapter
        spinner = findViewById(R.id.mainCourierList)
        spinner.onItemSelectedListener = this

        val llManager = LinearLayoutManager(this)

        historyAdapter = HistoryAdapter(ArrayList())
        mainHistories.apply {
            layoutManager = llManager
            adapter = historyAdapter
        }
            .addItemDecoration(DividerItemDecoration(this, llManager.orientation))
    }

    private fun setupLib() {
        viewModel = ViewModelProvider(
            this,
            Injector.provideViewModelFactory()
        ).get(MainViewModel::class.java)

        viewModel.historiesData.observe(this, historyObserver)
        viewModel.isChanged.observe(this, onTitleChange)
    }

    private fun readFromAsset(): List<Courier> {
        val fileName = "courier_list.json"

        val bufferReader = application.assets.open(fileName).bufferedReader()

        val jsonString = bufferReader.use {
            it.readText()
        }
        val gson = Gson()

        return gson.fromJson(jsonString, Array<Courier>::class.java).toList().filter {
            it.available
        }
    }

    private fun onAction() {
        mainButtonSearch.setOnClickListener {
            if (mainAWBInput.text.isNotEmpty()) {
                courierData?.let { it1 -> goToTracking(mainAWBInput.text.toString(), it1) }
            } else {
                showAlertDialog("Masukkan resi terlebih dahulu")
            }
        }

        historyAdapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                goToTracking(
                    historyAdapter.getData(position).awb,
                    historyAdapter.getData(position).courier
                )
            }

            override fun onDeleteMenuClick(position: Int) {
                val item = historyAdapter.getData(position)
                showSnackBar("${item.title ?: item.awb} Deleted")
            }

            override fun onEditMenuClick(position: Int) {
                showEditTitleDialog(historyAdapter.getData(position))
            }
        })
    }

    private fun goToTracking(awb: String, courier: Courier) {
        val intent = Intent(this, TrackingDetailActivity::class.java)
        intent.putExtra(COURIER_NAME, courier)
        intent.putExtra(AWB_NUMBER, awb)
        startActivity(intent)
    }

    private val historyObserver = Observer<List<History>> {
        historyAdapter.addList(it.reversed())
    }

    private val onTitleChange = Observer<Boolean> {
        if (it) {
            showSnackBar("Title changed")
        }
    }

    private fun showSnackBar(msg: String){
        Snackbar
            .make(
                mainCoordinatorLayout,
                msg,
                Snackbar.LENGTH_LONG
            )
            .show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        courierData = parent?.getItemAtPosition(position) as Courier
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun showEditTitleDialog(item: History) {
        val dialog = DialogEditTitle(object : DialogEditTitle.DialogListener {
            override fun onPositiveDialog(text: String?) {
                viewModel.editHistoryTitle(item.awb, text.toString())
            }
        })
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val prevDialog = supportFragmentManager.findFragmentByTag("dialog")
        if (prevDialog != null) {
            fragmentTransaction.remove(prevDialog)
        }
        fragmentTransaction.addToBackStack(null)
        dialog.show(fragmentTransaction, "dialog")
    }

    private fun showAlertDialog(msg: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(getString(R.string.alert_title))
        dialogBuilder.setMessage(msg)

        dialogBuilder.setPositiveButton(R.string.ok_button) { _, _ ->
        }

        dialogBuilder.show()
    }
}