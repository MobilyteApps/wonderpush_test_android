package com.example.wonderpushtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wonderpush.sdk.WonderPush
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Spinner click listener
        spinner.onItemSelectedListener = this
        // checkbox click
        chkSubscribe.setOnCheckedChangeListener(this)

        // Spinner Drop down elements
        val categories = ArrayList<String>()
        categories.add("<Empty>")
        categories.add("Male")
        categories.add("Female")


        // Creating adapter for spinner
        val dataAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        // attaching data adapter to spinner
        spinner.adapter = dataAdapter

        /////Setup WonderPush
        setupWonderPush()
        setspinnerValue(dataAdapter)
    }

    private fun setupWonderPush() {
        if (!WonderPush.isReady()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {

                }
            }, IntentFilter(WonderPush.INTENT_INTIALIZED))
        }
        //set Checkbox Status as Subscription status

        chkSubscribe.isChecked = WonderPush.isSubscribedToNotifications()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val properties = JSONObject()
        properties.put("string_gender", p0?.getItemAtPosition(p2).toString())

        WonderPush.putProperties(properties)
    }

    private fun setspinnerValue(dataAdapter: ArrayAdapter<String>) {
        val lastSavedValue: String = WonderPush.getPropertyValue("string_gender") as String
        val spinnerPosition: Int = dataAdapter.getPosition(lastSavedValue)
        spinner.setSelection(spinnerPosition)
    }

    override fun onCheckedChanged(parent: CompoundButton?, checked: Boolean) {
        if (checked) {
            WonderPush.subscribeToNotifications()
            Toast.makeText(this, "Push notifications subscribed", Toast.LENGTH_SHORT).show()
        } else {
            WonderPush.unsubscribeFromNotifications()
            Toast.makeText(this, "Push notifications unsubscribed", Toast.LENGTH_SHORT).show()
        }
    }
}
