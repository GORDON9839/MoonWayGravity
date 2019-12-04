package com.example.moonwaygravity.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moonwaygravity.Model.Vehicle;
import com.example.moonwaygravity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.vehicleViewHolder> {
    private Context context;
    private List<Vehicle> vehicle;

    FirebaseUser firebaseUser;

    public VehicleListAdapter(Context context, List<Vehicle> vehicle){
        this.vehicle = vehicle;
        this.context = context;
    }

    @NonNull
    @Override
    public VehicleListAdapter.vehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_list_layout,parent,false);

        return new VehicleListAdapter.vehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleListAdapter.vehicleViewHolder holder, int position) {
        Vehicle veh = vehicle.get(position);
        holder.vehicleLicensePlate.setText(veh.getVehicleLicensePlateNumber());

    }

    @Override
    public int getItemCount() {
        return vehicle.size();
    }

    public class vehicleViewHolder extends RecyclerView.ViewHolder{
        public TextView vehicleLicensePlate;
        public vehicleViewHolder(View itemView){
            super(itemView);
            vehicleLicensePlate = itemView.findViewById(R.id.licensePlate);
        }
    }

}
