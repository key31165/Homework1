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
        //εεΎζ¬ι
        checkPermission(context);
        Button btnScan = view.findViewById(R.id.btn_scan_startScan);
        BluetoothAdapter.LeScanCallback leScanCallback = getCallback(context);
        //θ¨­ε?ζιεζ
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

        //θ¨­ε?RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_scan_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ScanAdapter(scannedDevicesArrayList);
        recyclerView.setAdapter(mAdapter);
        //θ¨­ε?ζδΈιΈι ηεζ
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
     * ζ¬ιηΈιθͺθ­
     */
    private void checkPermission(Context context) {
        /*η’Ίθͺζζ©ηζ¬ζ―ε¦ε¨API18δ»₯δΈ*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*η’Ίθͺζ―ε¦ε·²ιεεεΎζζ©δ½η½?εθ½δ»₯εζ¬ι*/
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }
            /*η’Ίθͺζζ©ζ―ε¦ζ―ζ΄θη*/
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(context, "Not support Bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }
            /*ιεθθ½*/
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    /**
     * εε§θηζζεζζιιδΉηΈιεθ½
     */
    private BluetoothAdapter.LeScanCallback getCallback(Context context) {
        /**εη¨θηι©ιε¨*/
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /**
         * ι‘―η€Ίζζε°η©δ»Ά
         */
        Set<ScannedDevices> tempDeviceList = new HashSet<>();
        Set<String> addr = new HashSet<>();
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                new Thread(() -> {
                    /**ε¦ζθ£η½?ζ²ζεε­οΌε°±δΈι‘―η€Ί*/
                    ScannedDevices scannedDevices = new ScannedDevices(device, rssi);
                    if (device.getName() != null && !addr.contains(device.getAddress())) {
                        Log.d("test", "test device name: " + device.getName());
                        /**ε°ζε°ε°ηθ£η½?ε ε₯ι£ε*/
                        addr.add(device.getAddress());
                        tempDeviceList.add(scannedDevices);
                        /**ε°ι£εδΈ­ιθ€Addressηθ£η½?ζΏΎι€οΌδΈ¦δ½ΏδΉζηΊζζ°ζΈζ*/
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
