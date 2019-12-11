package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.moonwaygravity.Adapter.AvailableParkingAdapter;
import com.example.moonwaygravity.Adapter.MessageAdapter;
import com.example.moonwaygravity.Model.Block;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAvailableParking extends AppCompatActivity {
    final String parkingLotid = "-LtmhqyC5ACsE-k8dB8Q";
    RecyclerView TableRecycleView;
    Toolbar toolbar;
    FirebaseDatabase database;
    List<Block> blocks;
    AvailableParkingAdapter availableParkingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_available_parking);
        init();
        toolbar=findViewById(R.id.view_parking_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void init(){
        database = FirebaseDatabase.getInstance();
        TableRecycleView = findViewById(R.id.availble_parking_table);
        TableRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAvailableParking.this);
        linearLayoutManager.setStackFromEnd(true);
        TableRecycleView.setLayoutManager(linearLayoutManager);
        retrieveBlocks();
    }
    private void retrieveBlocks(){

        blocks=new ArrayList<Block>();
        DatabaseReference blocksRef = database.getReference("Blocks");
        blocksRef.orderByChild("parkingLotid").equalTo(parkingLotid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Block block = snapshot.getValue(Block.class);
                    block.setId(snapshot.getKey());
                    blocks.add(block);
                }

                availableParkingAdapter = new AvailableParkingAdapter(ViewAvailableParking.this, blocks);
                TableRecycleView.setAdapter(availableParkingAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
