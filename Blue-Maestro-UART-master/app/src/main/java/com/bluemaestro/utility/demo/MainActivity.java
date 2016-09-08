
/*
 * Copyright (c) 2016, Blue Maestro
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bluemaestro.utility.demo;




import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemaestro.utility.demo.devices.BMDevice;
import com.bluemaestro.utility.demo.devices.BMDeviceMap;
import com.bluemaestro.utility.demo.views.dialogs.BMAlertDialog;
import com.bluemaestro.utility.demo.views.graphs.BMLineChart;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    public static final String TAG = "BlueMaestro";

    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_HISTORY_DEVICE = 3;
    private static final int UART_PROFILE_READY = 10;

    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    private int mState = UART_PROFILE_DISCONNECTED;

    private UartService mService = null;

    private BluetoothDevice mDevice = null;
    private BMDevice mBMDevice = null;

    private BluetoothAdapter mBtAdapter = null;
    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private Button
            btnConnectDisconnect,
            btnSend, btnGraph;
    private EditText edtMessage;
    private BMLineChart lineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Delete all databases; testing only
        String[] addresses = getApplicationContext().databaseList();
        for(String address : addresses) {
            getApplicationContext().deleteDatabase(address);
        }

        View rootView = findViewById(android.R.id.content).getRootView();
        StyleOverride.setDefaultTextColor(rootView, Color.BLACK);
        StyleOverride.setDefaultFont(rootView, this, "Montserrat-Regular.ttf");

        setContentView(R.layout.main);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);

        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);

        btnConnectDisconnect = (Button) findViewById(R.id.btn_select);
        btnSend = (Button) findViewById(R.id.sendButton);
        btnGraph = (Button) findViewById(R.id.graphButton);
        edtMessage = (EditText) findViewById(R.id.sendText);
        lineChart = (BMLineChart) findViewById(R.id.lineChart);

        // Initialise UART service
        service_init();
       
        // Handle Disconnect & Connect button
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {onClickConnectDisconnect();
            }
        });
        // Handle Send button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSend();
            }
        });
        // Handle Graph button
        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGraph();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        if(mService != null){
            mService.stopSelf();
            mService = null;
        }
        BMDeviceMap.INSTANCE.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                // When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    mBMDevice = BMDeviceMap.INSTANCE.getBMDevice(mDevice.getAddress());
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - connecting");
                    mService.connect(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_HISTORY_DEVICE:
                // When the HistoryListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    btnGraph.setEnabled(true);
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    /************************** INITIALISE **************************/

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    /************************** UART STATUS CHANGE **************************/

    // UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    // Main UART broadcast receiver
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                onGattConnected();
            } else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                onGattDisconnected();
            } else if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                onGattServicesDiscovered();
            } else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                onDataAvailable(intent.getByteArrayExtra(UartService.EXTRA_DATA));
            } else if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                onDeviceDoesNotSupportUART();
            } else{

            }
        }
    };

    private void onGattConnected() {
        runOnUiThread(new Runnable() {
            public void run() {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "UART_CONNECT_MSG");
                btnConnectDisconnect.setText("Disconnect");
                edtMessage.setEnabled(true);
                btnSend.setEnabled(true);
                btnGraph.setEnabled(true);
                lineChart.clear();
                ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - ready");
                listAdapter.add("[" + currentDateTimeString + "] Connected to: " + mDevice.getName());
                messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                mState = UART_PROFILE_CONNECTED;
            }
        });
    }

    private void onGattDisconnected() {
        runOnUiThread(new Runnable() {
            public void run() {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "UART_DISCONNECT_MSG");
                btnConnectDisconnect.setText("Connect");
                edtMessage.setEnabled(false);
                btnSend.setEnabled(false);
                btnGraph.setEnabled(false);
                ((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                listAdapter.add("[" + currentDateTimeString + "] Disconnected from: " + mDevice.getName());
                mState = UART_PROFILE_DISCONNECTED;
                messageListView.setSelection(listAdapter.getCount() - 1);
                mService.close();
            }
        });
    }

    private void onGattServicesDiscovered(){
        mService.enableTXNotification();
    }

    private void onDataAvailable(final byte[] txValue) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    String text = new String(txValue, "UTF-8").trim();
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    listAdapter.add("[" + currentDateTimeString + "] RX: " + text);
                    if (messageListView.getVisibility() == View.VISIBLE) {
                        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                    } else {
                        messageListView.setSelection(listAdapter.getCount() - 1);
                    }

                    if (mBMDevice == null) return;
                    mBMDevice.updateChart(lineChart, text);
                    //mBMDatabase.addData(BMDatabase.TIMESTAMP_NOW(), text);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void onDeviceDoesNotSupportUART() {
        showMessage("Device doesn't support UART. Disconnecting");
        mService.disconnect();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    /************************** BUTTON CLICK HANDLERS **************************/

    private void onClickConnectDisconnect(){
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (btnConnectDisconnect.getText().equals("Connect")) {
                // Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
                Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                edtMessage.setText("");
                edtMessage.setVisibility(View.VISIBLE);
                messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
            } else {
                // Disconnect button pressed
                if (mDevice != null) mService.disconnect();
                mBMDevice = null;
                lineChart.setVisibility(View.GONE);
                messageListView.setVisibility(View.VISIBLE);
                messageListView.setSelection(listAdapter.getCount() - 1);
            }
        }
    }

    private void onClickSend(){
        EditText editText = (EditText) findViewById(R.id.sendText);

        String message = editText.getText().toString().trim();
        byte[] value;
        try {
            // Send data to service
            value = message.getBytes("UTF-8");
            mService.writeRXCharacteristic(value);
            // Update the log with time stamp
            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
            listAdapter.add("[" + currentDateTimeString + "] TX: " + message);
            messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
            edtMessage.setText("");

            //lineChart.setVisibility(View.GONE);
            //messageListView.setVisibility(View.VISIBLE);
            messageListView.setSelection(listAdapter.getCount() - 1);

            if(mBMDevice == null) return;
            mBMDevice.setupChart(lineChart, message);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void onClickGraph(){
        switch(lineChart.getVisibility()){
            case View.VISIBLE:
                lineChart.setVisibility(View.GONE);
                messageListView.setVisibility(View.VISIBLE);
                Log.d(TAG, "Graph gone");
                break;
            case View.GONE:
                lineChart.setVisibility(View.VISIBLE);
                messageListView.setVisibility(View.GONE);
                Log.d(TAG, "Graph visible");
                break;
            default:
                Log.d(TAG, "Graph invisible");
                break;
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("Blue Maestro Utility App is running in background. Disconnect to exit");
        }
        else {
            BMAlertDialog dialog = new BMAlertDialog(this,
                    "",
                    "Do you want to quit this Application?");
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setNegativeButton("NO", null);

            dialog.show();
            dialog.applyFont(this, "Montserrat-Regular.ttf");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    /*private Handler mHandler = new Handler() {
        @Override

        // Handler events that received from UART service
        public void handleMessage(Message msg) {

        }
    };*/
}
