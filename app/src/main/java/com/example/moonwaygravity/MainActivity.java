package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    createPagerAdapter adapter;
    TextView logout;
    Toolbar toolbar;
    AppCompatImageView logo;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, PushNotification.class);
        startService(intent);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon((ContextCompat.getDrawable(this, R.drawable.menu)));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        logo = findViewById(R.id.logo);
        adapter = new createPagerAdapter
                (this, getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, CustomerLoginActivity.class));

            case R.id.view_parking:
                Intent intent = new Intent(MainActivity.this, ViewAvailableParking.class);
                startActivity(intent);
                return true;

            case R.id.user_profile:
                Intent intentProfile = new Intent(MainActivity.this, ManageProfile.class);
                startActivity(intentProfile);

                return true;
        }
        return false;
    }


}
