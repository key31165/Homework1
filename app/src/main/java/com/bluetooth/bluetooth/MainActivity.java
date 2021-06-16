package com.bluetooth.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.bluetooth.bluetooth.scan.ScanActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_enter = findViewById(R.id.btn_main_enter);
        //前往掃描的Activity
        btn_enter.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        });
    }
}