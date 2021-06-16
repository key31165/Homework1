package com.bluetooth.bluetooth.scan;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.bluetooth.R;

import java.util.List;

public class ScanAdapter extends RecyclerView.Adapter<ScanViewHolder> {
    private final List<ScannedDevices> scannedDevices;
    public ScanAdapter(List<ScannedDevices> scannedDevices){
        this.scannedDevices = scannedDevices;
    }


    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bluetooth,parent,false);
        return new ScanViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {
        holder.bind(scannedDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return scannedDevices.size();
    }
}
