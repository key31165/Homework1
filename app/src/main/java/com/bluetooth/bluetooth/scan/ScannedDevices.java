package com.bluetooth.bluetooth.scan;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class ScannedDevices implements Serializable {
    public BluetoothDevice bluetoothDevice;
    public int rssi;

    public ScannedDevices(BluetoothDevice bluetoothDevice, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
