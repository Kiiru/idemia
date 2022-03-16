package com.idemia.idscreen;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idemia.idscreen.adapters.HomeScreenItemsAdapter;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HomeScreenItemsAdapter homeScreenItemsAdapter;
    private ArrayList<String> featureList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.home_activity_action_bar);
        }
        recyclerView = findViewById(R.id.home_screen_recycler_list);
        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        featureList = Utility.getFeatures();
        if (!Utility.isIrisSensorAvailable()) {
            featureList.remove(4);
        }
        homeScreenItemsAdapter = new HomeScreenItemsAdapter(HomeScreen.this, featureList);
        recyclerView.setAdapter(homeScreenItemsAdapter);
    }
}
