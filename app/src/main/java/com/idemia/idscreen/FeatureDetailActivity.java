package com.idemia.idscreen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

public class FeatureDetailActivity extends AppCompatActivity {
    private String title = "";
    private AppCompatTextView title_tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        title_tv = toolbar.findViewById(R.id.action_bar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("TITLE");
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            title_tv.setText(title);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
