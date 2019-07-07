package com.example.btdemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ParedDevicesAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<BluetoothDevice> names;
    CallbackMethods callbackMethods;


    public ParedDevicesAdaptor(Activity activity, List<BluetoothDevice> names, CallbackMethods callbackMethods) {
        this.activity = activity;
        this.names = names;
        this.callbackMethods = callbackMethods;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_device, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        myViewHolder.mName.setText(names.get(myViewHolder.getAdapterPosition()).getName());

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tv_name);
            mName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callbackMethods.SelectDevice(getLayoutPosition());
        }
    }
}
