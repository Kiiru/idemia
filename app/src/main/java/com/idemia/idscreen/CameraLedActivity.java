package com.idemia.idscreen;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.idemia.ledservice.PortInterface;

public class CameraLedActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private final String TAG = "CameraLedActivity";
    private String title = "";
    private AppCompatTextView title_tv = null;
    private boolean resetLeds = false;

    private PortInterface mPortInterface = null;

    private TextView upTorch_tv = null;
    private TextView downTorch_tv = null;
    private TextView upDefaultTorch_tv = null;
    private TextView downDefaultTorch_tv = null;
    private TextView upFlash_tv = null;
    private TextView downFlash_tv = null;

    private SeekBar upTorch_bar = null;
    private SeekBar downTorch_bar = null;
    private SeekBar upDefaultTorch_bar = null;
    private SeekBar downDefaultTorch_bar = null;
    private SeekBar upFlash_bar = null;
    private SeekBar downFlash_bar = null;

    private Button openCamera_btn = null;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "aidl disconnected");
            mPortInterface = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "aidl connect success");
            mPortInterface = PortInterface.Stub.asInterface(service);
            if (resetLeds) {
                resetLeds = false;
                Log.d(TAG, "Resetting leds");
                try {
                    mPortInterface.setDefaultTorchLed(31, 31);
                    mPortInterface.setFlashLed(127, 127);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_led);
        Toolbar toolbar = findViewById(R.id.toolbar);
        title_tv = toolbar.findViewById(R.id.action_bar_title);
        setSupportActionBar(toolbar);
        resetLeds = true;

        ActionBar actionBar = getSupportActionBar();
        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("TITLE");
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            title_tv.setText(title);
        }

        // Initialize UI
        // Visibile LEDs
        upTorch_tv = findViewById(R.id.up_torch_tv);
        upTorch_bar = findViewById(R.id.up_torch_bar);
        upTorch_bar.setOnSeekBarChangeListener(this);
        downTorch_tv = findViewById(R.id.down_torch_tv);
        downTorch_bar = findViewById(R.id.down_torch_bar);
        downTorch_bar.setOnSeekBarChangeListener(this);

        upDefaultTorch_tv = findViewById(R.id.up_default_torch_tv);
        upDefaultTorch_bar = findViewById(R.id.up_default_torch_bar);
        upDefaultTorch_bar.setOnSeekBarChangeListener(this);
        downDefaultTorch_tv = findViewById(R.id.down_default_torch_tv);
        downDefaultTorch_bar = findViewById(R.id.down_default_torch_bar);
        downDefaultTorch_bar.setOnSeekBarChangeListener(this);

        upFlash_tv = findViewById(R.id.up_flash_tv);
        upFlash_bar = findViewById(R.id.up_flash_bar);
        upFlash_bar.setOnSeekBarChangeListener(this);
        downFlash_tv = findViewById(R.id.down_flash_tv);
        downFlash_bar = findViewById(R.id.down_flash_bar);
        downFlash_bar.setOnSeekBarChangeListener(this);

        openCamera_btn = findViewById(R.id.open_camera_btn);
        openCamera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.mediatek.camera", "com.mediatek.camera.CameraLauncher"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int seekbarId = seekBar.getId();
        int up = 0;
        int down = 0;

        if (seekbarId == upTorch_bar.getId() || seekbarId == downTorch_bar.getId()){
            up = upTorch_bar.getProgress();
            down = downTorch_bar.getProgress();
            upTorch_tv.setText("Up LED value: " + up);
            downTorch_tv.setText("Down LED value: " + down);
            try {
                if (mPortInterface != null) {
                    mPortInterface.setTorchLed(up, down);
                    Log.d(TAG, "set torch led");
                } else {
                    Log.e(TAG, "ERROR torch LED");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else if (seekbarId == upDefaultTorch_bar.getId() || seekbarId == downDefaultTorch_bar.getId()){
            up = upDefaultTorch_bar.getProgress();
            down = downDefaultTorch_bar.getProgress();
            upDefaultTorch_tv.setText("Up LED value: " + up);
            downDefaultTorch_tv.setText("Down LED value: " + down);
            try {
                if (mPortInterface != null) {
                    mPortInterface.setDefaultTorchLed(up, down);
                    Log.d(TAG, "set default torch led");
                } else {
                    Log.e(TAG, "ERROR default torch LED");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else if (seekbarId == upFlash_bar.getId() || seekbarId == downFlash_bar.getId()){
            up = upFlash_bar.getProgress();
            down = downFlash_bar.getProgress();
            upFlash_tv.setText("Up LED value: " + up);
            downFlash_tv.setText("Down LED value: " + down);
            try {
                if (mPortInterface != null) {
                    mPortInterface.setFlashLed(up, down);
                    Log.d(TAG, "set flash led");
                } else {
                    Log.e(TAG, "ERROR flash LED");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void stopTorchLed(){
        try {
            if(mPortInterface != null) {
                mPortInterface.setTorchLed(0, 0);
                upTorch_tv.setText("Up LED value: 0");
                downTorch_tv.setText("Down LED value: 0");
            }
            else{
                Log.e(TAG, "Failed to stop LEDs");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Intent getAidlIntent() {
        Intent aidlIntent = new Intent();
        aidlIntent.setAction("idemia.intent.action.CONN_LED_SERVICE_AIDL");
        aidlIntent.setPackage("com.idemia.ledservice");
        return aidlIntent;
    }

    @Override
    protected void onResume() {
        if(!bindService(getAidlIntent(), serviceConn, Service.BIND_AUTO_CREATE)){
            Log.e(TAG, "System couldn't find the service");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTorchLed();
        unbindService(serviceConn);
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
