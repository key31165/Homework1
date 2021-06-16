package com.bluetooth.bluetooth.scan;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.bluetooth.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ScanFragment extends Fragment {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 5566;
    private static final int REQUEST_ENABLE_BT = 5577;
    private boolean isScanning = false;
    ScanAdapter mAdapter;
    private final ArrayList<ScannedDevices> scannedDevicesArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        Activity activity = getActivity();
        if (context == null || activity == null) return;
        //取得權限
        checkPermission(context);
        Button btnScan = view.findViewById(R.id.btn_scan_startScan);
        BluetoothAdapter.LeScanCallback leScanCallback = getCallback(context);
        //設定按鈕回應
        btnScan.setOnClickListener(buttonView -> {
            isScanning = !isScanning;
            if (isScanning) {
                btnScan.setText(context.getString(R.string.close_scan));
                mBluetoothAdapter.startLeScan(leScanCallback);
            } else {
                btnScan.setText(context.getString(R.string.start_scan));
                mBluetoothAdapter.stopLeScan(leScanCallback);
            }
        });

        //設定RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_scan_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ScanAdapter(scannedDevicesArrayList);
        recyclerView.setAdapter(mAdapter);
        //設定按下選項的反應
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("device",scannedDevicesArrayList.get(position));
                Navigation.findNavController(view).navigate(R.id.action_scanFragment_to_detailFragment,bundle);
            }
        }));
    }

    /**
     * 權限相關認證
     */
    private void checkPermission(Context context) {
        /*確認手機版本是否在API18以上*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*確認是否已開啟取得手機位置功能以及權限*/
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }
            /*確認手機是否支援藍牙*/
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(context, "Not support Bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }
            /*開啟藍芽*/
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    /**
     * 初始藍牙掃描及掃描開關之相關功能
     */
    private BluetoothAdapter.LeScanCallback getCallback(Context context) {
        /**啟用藍牙適配器*/
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /**
         * 顯示掃描到物件
         */
        Set<ScannedDevices> tempDeviceList = new HashSet<>();
        Set<String> addr = new HashSet<>();
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                new Thread(() -> {
                    /**如果裝置沒有名字，就不顯示*/
                    ScannedDevices scannedDevices = new ScannedDevices(device, rssi);
                    if (device.getName() != null && !addr.contains(device.getAddress())) {
                        Log.d("test", "test device name: " + device.getName());
                        /**將搜尋到的裝置加入陣列*/
                        addr.add(device.getAddress());
                        tempDeviceList.add(scannedDevices);
                        /**將陣列中重複Address的裝置濾除，並使之成為最新數據*/
                        //ArrayList<ScannedDevices> newList = getSingle(tempDeviceList);
                        ArrayList<ScannedDevices> newList = new ArrayList<>(tempDeviceList);
                        scannedDevicesArrayList.clear();
                        scannedDevicesArrayList.addAll(newList);
                        getActivity().runOnUiThread(() -> {
                            mAdapter.notifyDataSetChanged();
                        });
                    }
                }).start();
            }
        };
    }
}
