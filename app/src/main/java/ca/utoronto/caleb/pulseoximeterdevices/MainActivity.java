package ca.utoronto.caleb.pulseoximeterdevices


import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.FirebaseApp

import java.util.ArrayList
import java.util.HashMap


class MainActivity : Activity(), DescriptionRequester {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    internal var mUsbManager: UsbManager

    internal var mDevices: ArrayList<Device>

    private var mPermissionIntent: PendingIntent? = null
    private val mUsbReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            synchronized(this) {
                if (ACTION_USB_PERMISSION == action) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Toast.makeText(this@MainActivity, R.string.device_granted, Toast.LENGTH_LONG).show()
                            addDevice(device)
                        }
                    } else {
                        Log.d(TAG, "permission denied for device" + device!!)
                        finish()
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                    Log.d(TAG, "USB DEVICE ATTACHED")
                    if (device != null) {
                        addDevice(device)
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                    Log.d(TAG, "Usb device DETACHED")
                    setupUsbDevices()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDevices = ArrayList()
        setupRecyclerView()
        mUsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        setupUsbPermissionHandler()
        setupUsbDevices()
        FirebaseApp.initializeApp(this)
    }

    private fun setupRecyclerView() {
        mRecyclerView = findViewById(R.id.device_list_view)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = DeviceAdapter(mDevices, this)
        mRecyclerView!!.adapter = mAdapter
    }

    private fun setupUsbPermissionHandler() {
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(mUsbReceiver, filter)
    }

    private fun setupUsbDevices() {
        mDevices.clear()
        val deviceList = mUsbManager.deviceList
        val it = deviceList.values.iterator()
        while (it.hasNext()) {
            val usbDevice = it.next() as UsbDevice
            addDevice(usbDevice)
            it.remove()
        }
    }

    private fun addDevice(usbDevice: UsbDevice?) {
        val device = Device(usbDevice!!)
        if (device.isValid) {
            if (!this.mUsbManager.hasPermission(usbDevice)) {
                this.mUsbManager.requestPermission(usbDevice, this.mPermissionIntent)
            } else {
                mDevices.add(device)
                mAdapter!!.notifyDataSetChanged()
            }
        }
    }


    fun onClickLogUsbDevices(v: View) {
        val deviceList = mUsbManager.deviceList
        Log.d(TAG, deviceList.size.toString() + " devices found.")
        val it = deviceList.values.iterator()
        while (it.hasNext()) {
            val device = it.next() as UsbDevice
            val deviceName = device.deviceName
            Log.d(TAG, "Device Name: $deviceName")
            val vendorID = device.vendorId
            val productID = device.productId
            Log.d(TAG, "Vendor ID: $vendorID, Product ID: $productID")
            val productName = device.productName
            Log.d(TAG, "Product Name: " + productName!!)
            val manufacturerName = device.manufacturerName
            Log.d(TAG, "Manufacturer Name: " + manufacturerName!!)
            val serial = device.serialNumber
            Log.d(TAG, "Serial No: " + device.serialNumber!!)
        }
    }

    fun onClickMonitorBtn(v: View) {
        startMonitorActivity()
    }

    private fun startMonitorActivity() {

        val intent = Intent(this, MonitorActivity::class.java)
        if (mDevices.size > 0) {
            intent.putParcelableArrayListExtra(DEVICE_LIST_PARAM, mDevices)
            startActivity(intent)
        } else {
            Toast.makeText(this@MainActivity, R.string.no_devices, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mUsbReceiver)
    }

    override fun requestUserDescription(device: Device) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter a description of this device.")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val desc = input.text.toString()
            if (!desc.trim { it <= ' ' }.isEmpty()) {
                device.userDescription = desc
                this@MainActivity.mAdapter!!.notifyDataSetChanged()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    fun onClickRefreshBtn(v: View) {
        setupUsbDevices()
    }

    companion object {

        val TAG = "PULSE_OXIMETER_DEVICES"

        val DEVICE_LIST_PARAM = "com.utoronto.caleb.pulseoximeterapp.param.DEVICE_LIST_PARAMETER"

        private val ACTION_USB_PERMISSION = "com.utoronto.caleb.pulseoximeterapp.action.USB_PERMISSION"
    }
}