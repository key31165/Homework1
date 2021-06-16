package com.bluetooth.bluetooth.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.bluetooth.R;
import com.bluetooth.bluetooth.scan.ScannedDevices;

public class DetailFragment extends Fragment {
    private String getRssi(int rssi) {
        if (rssi > -50) {
            return "3";
        } else if (rssi > -70) {
            return "2";
        } else {
            return "1";
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle == null){
            Log.d("test","bundle is null....");
        }
        ScannedDevices scannedDevices = (ScannedDevices)bundle.getSerializable("device");
        TextView tvDeviceName = view.findViewById(R.id.tv_deviceName);
        TextView tvRSSI = view.findViewById(R.id.tv_rssi);
        TextView tvMac = view.findViewById(R.id.tv_mac);
        tvDeviceName.setText(scannedDevices.getBluetoothDevice().getName());
        tvRSSI.setText(getRssi(scannedDevices.getRssi()));
        tvMac.setText(scannedDevices.getBluetoothDevice().getAddress());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail,container,false);
    }


}
