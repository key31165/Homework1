package com.bluetooth.bluetooth.scan;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.bluetooth.R;
import com.bluetooth.bluetooth.scan.ScannedDevices;

public class ScanViewHolder extends RecyclerView.ViewHolder {
    TextView tv_mac;
    TextView tv_rssi;

    public ScanViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_mac = itemView.findViewById(R.id.tv_detail_mac);
        tv_rssi = itemView.findViewById(R.id.tv_detail_rssi);
    }

    private int getRssi(int rssi) {
        if (rssi > -50) {
            return 3;
        } else if (rssi > -70) {
            return 2;
        } else {
            return 1;
        }
    }

    private String getMac(ScannedDevices scannedDevices) {
        return "裝置位址: " + scannedDevices.bluetoothDevice.getAddress();
    }

    private String getRssi(ScannedDevices scannedDevices){
        return "接收訊號強度: "+ getRssi(scannedDevices.rssi) + "(數字越大訊號越強)";
    }
    public void bind(ScannedDevices scannedDevices) {
        tv_mac.setText(getMac(scannedDevices));
        tv_rssi.setText(getRssi(scannedDevices));
    }
}
