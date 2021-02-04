package com.example.serialportapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.serialportapplication.util.SerialPortHelper;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {


    private Button btn_send;
    private SerialPortHelper serialPortHelper;
    private Button btnSend;
    private TextView tvData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 0);
        }
        serialPortHelper = SerialPortHelper.getInstance().init("/dev/ttyS1", 9600, 0);
        serialPortHelper.setOnReceiveDataListener(new SerialPortHelper.OnReceiveDataListener() {
            @Override
            public void OnReceive(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvData.setText(tvData.getText().toString()+"\n"+data);
                    }
                });
            }

            @Override
            public void OnReceive(byte[] data) {
                Log.i("-------------", "OnReceive: data.length="+data.length);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    serialPortHelper.sendData(new String("i am com2".getBytes(), "GBK"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private void initView() {
        btn_send = (Button) findViewById(R.id.btn_send);
        btnSend = (Button) findViewById(R.id.btn_send);
        tvData = (TextView) findViewById(R.id.tv_data);
    }

    @Override
    protected void onDestroy() {
        serialPortHelper.destroy();
        super.onDestroy();
    }
}
