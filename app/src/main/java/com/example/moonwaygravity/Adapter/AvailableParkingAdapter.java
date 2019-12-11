package com.example.moonwaygravity.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moonwaygravity.Model.Block;
import com.example.moonwaygravity.Model.Component;
import com.example.moonwaygravity.Model.Floor;
import com.example.moonwaygravity.Model.Message;
import com.example.moonwaygravity.Model.ParkingSlot;
import com.example.moonwaygravity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AvailableParkingAdapter extends RecyclerView.Adapter<AvailableParkingAdapter.ViewHolder> {
    FirebaseDatabase database;
    private Context context;
    private List<Block> blocks;
    private List<Floor> floors;
    private List<Component> components;
    private List<ParkingSlot> parkingSlots;
    FirebaseUser firebaseUser;
    public int parkingSlotSum = 0;
    TableRow tr;
    TextView floorNo, availableParkingSlot;
    TableLayout table;

    public AvailableParkingAdapter(Context context, List<Block> blocks) {
        floors = new ArrayList<Floor>();
        components = new ArrayList<Component>();
        parkingSlots = new ArrayList<ParkingSlot>();
        this.blocks = blocks;
        this.context = context;
    }

    @NonNull
    @Override
    public AvailableParkingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.available_parking_item, parent, false);

        return new AvailableParkingAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AvailableParkingAdapter.ViewHolder holder, int position) {
        //initalize later use component

        database = FirebaseDatabase.getInstance();
        //initalize parameter will use
        final Block block = blocks.get(position);
        final DatabaseReference floorRef = database.getReference("Floors");
        final DatabaseReference compRef = database.getReference("Comps");
        final DatabaseReference parkingSlotRef = database.getReference("ParkingSlot");
        holder.blockLabel.setText("Block:" + block.getBlockName());
        floorRef.orderByChild("blockid").equalTo(block.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot fsnap : dataSnapshot.getChildren()) {
                    Floor floor = fsnap.getValue(Floor.class);
                    floor.setId(fsnap.getKey().toString());
                    Log.d("hmmmid",floor.getId());
                    floors.add(floor);

                    compRef.orderByChild("floorid").equalTo(floor.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count =0;
                            for (DataSnapshot csnap : dataSnapshot.getChildren()) {

                                Component component = csnap.getValue(Component.class);
                                component.setId(csnap.getKey());

                                if (component.getName().toLowerCase().equals("slotvertical") || component.getName().toLowerCase().equals("slothorinzontal")) {
                                    count++;

                                    components.add(component);
                                    parkingSlotRef.orderByChild("compid").equalTo(component.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot psnap : dataSnapshot.getChildren()) {
                                                ParkingSlot parkingSlot = psnap.getValue(ParkingSlot.class);
                                                parkingSlot.setId(psnap.getKey().toString());
                                                if (parkingSlot.getStatus() != null) {
                                                    if (parkingSlot.getStatus().toLowerCase().equals("available") || parkingSlot.getStatus().equals("")) {
                                                        parkingSlots.add(parkingSlot);

                                                    }
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        initTable(holder, position);
                    }
                },
                3000);
    }

    @Override
    public int getItemCount() {
        return blocks.size();
    }

    private void initTable(AvailableParkingAdapter.ViewHolder holder, int position) {

        if (floors.size() > 0 && components.size() > 0 && parkingSlots.size() > 0) {
            for (Floor f : floors) {
                parkingSlotSum = 0;
                for (Component c : components) {
                    if (f.getId().equals(c.getFloorid())) {
                        for (ParkingSlot ps : parkingSlots) {
                            if (ps.getCompid().equals(c.getId())) {
                                parkingSlotSum += 1;
                            }
                        }
                    }
                }
                floorNo = new TextView(context);
                availableParkingSlot = new TextView(context);
                floorNo.setText(f.getFloorName().toUpperCase());
                floorNo.setTextSize(20);
                floorNo.setGravity(Gravity.CENTER);
                availableParkingSlot.setText(String.valueOf(parkingSlotSum));
                availableParkingSlot.setTextSize(20);
                availableParkingSlot.setGravity(Gravity.CENTER);
                tr = new TableRow(context);
                tr.addView(floorNo);
                tr.addView(availableParkingSlot);
                holder.mtable.addView(tr);
            }
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TableLayout mtable;
        TextView blockLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mtable = itemView.findViewById(R.id.availble_table);
            blockLabel = itemView.findViewById(R.id.blockLabel);

        }
    }
}
