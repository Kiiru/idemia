package com.idemia.idscreen;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button startBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = findViewById(R.id.start_btn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        ((AppCompatTextView)findViewById(R.id.versionText)).setText("Version " + BuildConfig.VERSION_NAME);

        if (!Utility.isSecureFirmware()) {
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage(getString(R.string.incompatibleFirmware));
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", (DialogInterface.OnClickListener) null);
            alertDialog.show();
        }
    }
}
