package com.example.btdemo;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvStatus = findViewById(R.id.tv_status);

        checkBluetoothStatus();

    }

    private void checkBluetoothStatus() {

        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter == null) {
            mTvStatus.setText(getString(R.string.notSupported));

        } else {

            if (!bAdapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);

            } else {

                proccedToNextActivity();
            }
        }
    }

    private void proccedToNextActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashActivity.this, "Procedeed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, ConnectDeviceActivity.class));
                finishAffinity();

            }
        }, 2000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode != RESULT_OK) {

                Toast.makeText(this, "You have to enable bluetooth", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);


            } else {

                Toast.makeText(this, "Bluetooth Turned ON!", Toast.LENGTH_SHORT).show();
                proccedToNextActivity();
            }
        }

    }
}
