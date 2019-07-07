package com.example.btdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

public class ConnectDeviceActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mLoad, btn_scan, clickButton;
    private BluetoothAdapter bluetoothAdapter;
    private Activity activity;
    private List<BluetoothDevice> paredDevices = new ArrayList<>();
    private ParedDevicesAdaptor adaptor;
    private TextView dataText;
    private EditText inputData;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTING_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVE = 5;
    private static final String APP_NAME = "BtDemo";
    private static final UUID uuid = UUID.fromString("b79dc694-17e5-4aab-8cae-3461b4fa2ec2");

    private SendRecive sendRecive;
    private BluetoothSocket socket;
    private BluetoothSocket client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        mRecyclerView = findViewById(R.id.rv_PairedDevices);
        mLoad = findViewById(R.id.btn_load);
        activity = ConnectDeviceActivity.this;

        dataText = findViewById(R.id.dataText);
        clickButton = findViewById(R.id.clickButton);


        inputData = findViewById(R.id.inputData);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setAdaptor(paredDevices);

        ServerClass serverClass = new ServerClass();
        serverClass.start();


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    dataText.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    dataText.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    dataText.setText("Connected");
                    break;
                case STATE_CONNECTING_FAILED:
                    dataText.setText("Failed");
                    break;

                case STATE_MESSAGE_RECEIVE:
                    byte[] readBuf = (byte[]) msg.obj;
                    String tempMsg = new String(readBuf, 0, msg.arg1);
                    if (tempMsg.equalsIgnoreCase("***")) {
                        Toast.makeText(activity, "key is " + "1234", Toast.LENGTH_SHORT).show();
                        Intent auth = new Intent(activity, AuthActivity.class);
                        startActivityForResult(auth, 100);

                    } else if (tempMsg.equalsIgnoreCase("Sucess***")) {

                        dataText.setText("Auth SucessFull");
                        clickButton.setEnabled(true);

                    } else if (tempMsg.equalsIgnoreCase("Fail***")) {

                        dataText.setText("Auth Faield");

                        clickButton.setEnabled(false);
                        dataText.setText("Auth failed");

                    } else {
                        dataText.setText(tempMsg);
                    }
                    break;
            }
            return true;
        }
    });


    public void loadParedDevices(View view) {

        switch (view.getId()) {
            case R.id.btn_load:
                LoadDevices();
                break;

            case R.id.btn_scan:
                ScanDevices();
                break;

            case R.id.clickButton:
                SendMsg();
                break;
        }
    }

    private void SendMsg() {

        String data = inputData.getText().toString();
        sendRecive.write(data.getBytes());
    }

    private void senKey() {

        String data = "***";
        sendRecive.write(data.getBytes());
    }

    private void ScanDevices() {
        if (paredDevices != null) {
            paredDevices.clear();
        }
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

//        }


    }

    private void LoadDevices() {
        if (paredDevices != null) {
            paredDevices.clear();
        }
        Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bluetoothDevice : bluetoothDeviceSet) {

            paredDevices.add(bluetoothDevice);

        }

        adaptor.notifyDataSetChanged();
    }

    private void setAdaptor(final List<BluetoothDevice> paredDevices) {

        adaptor = new ParedDevicesAdaptor(activity, paredDevices, new CallbackMethods() {
            @Override
            public void SelectDevice(int position) {
                ClientClass clientClass = new ClientClass(paredDevices.get(position));
                clientClass.start();
                dataText.setText("Connecting.....");
            }
        });
        mRecyclerView.setAdapter(adaptor);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // A Bluetooth device was found
                // Getting device information from the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                paredDevices.add(device);
                adaptor.notifyDataSetChanged();
                Log.i("Data", "Device found: " + device.getName() + "; MAC " + device.getAddress());
            }
        }
    };

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, uuid);
            } catch (IOException e) {


            }

        }

        public void run() {

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();


                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {


                    sendRecive = new SendRecive(socket);
                    sendRecive.start();


                    senKey();

                    Log.d("state", "connected");
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    break;

                }

            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;


        public ClientClass(BluetoothDevice bluetoothDevice) {
            device = bluetoothDevice;
            try {
                client = device.createInsecureRfcommSocketToServiceRecord(uuid);

                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTING_FAILED;
                handler.sendMessage(message);
            }
        }

        public void run() {
            try {
                client.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendRecive = new SendRecive(client);
                sendRecive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTING_FAILED;
                handler.sendMessage(message);

            }
        }
    }

    private class SendRecive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendRecive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {

                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVE, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if (key.equalsIgnoreCase("1234")) {
                    Toast.makeText(activity, "Auth Sucessful", Toast.LENGTH_SHORT).show();
                    clickButton.setEnabled(true);
                    sendSucess();
                } else {
                    try {
                        sendFailed();
                        clickButton.setEnabled(false);
                        dataText.setText("Auth failed");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    private void sendFailed() {

        String data = "Fail***";
        sendRecive.write(data.getBytes());
    }

    private void sendSucess() {

        String data = "Sucess***";
        sendRecive.write(data.getBytes());
    }
}


