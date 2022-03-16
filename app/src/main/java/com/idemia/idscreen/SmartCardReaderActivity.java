package com.idemia.idscreen;

import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.idemia.smartcardlibrary.SmartCardLibrary;

public class SmartCardReaderActivity extends AppCompatActivity implements SmartCardLibrary.ContactCardCallback {
    private final String TAG = "SmartCardReaderActivity";
    private String title = "";
    private AppCompatTextView title_tv = null;
    private AppCompatTextView apdu_tv = null;
    private AppCompatEditText apduToSend_et = null;

    private final static int GET_ATR = 1;
    private final static int WARM_RESET = 2;
    private final static int COLD_RESET = 3;

    private AppCompatButton openSamReader_bt = null;
    private AppCompatButton openSmartCardReader_bt = null;
    private AppCompatButton coldReset_bt = null;
    private AppCompatButton warmReset_bt = null;
    private AppCompatButton getAtr_bt = null;
    private AppCompatButton sendApdu_bt = null;
    private AppCompatButton closeReader_bt = null;;

    private boolean selectedSam = false;
    private boolean selectedSmartcard = false;
    private boolean readerOpen = false;
    private boolean cardInserted = false;
    private boolean cardPowerOn = false;

    private SmartCardLibrary sclibSmartcard = new SmartCardLibrary(this, SmartCardLibrary.READER_TYPE_SMARTCARD);
    private SmartCardLibrary sclibSam = new SmartCardLibrary(this, SmartCardLibrary.READER_TYPE_SAM);
    private SmartCardLibrary sclib = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_card_reader);
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

        // Initialize UI
        apduToSend_et = findViewById(R.id.apdu_to_send_et);
        apdu_tv = findViewById(R.id.apdu_tv);
        apdu_tv.setMovementMethod(new ScrollingMovementMethod());

        openSamReader_bt = findViewById(R.id.open_sam_reader_btn);
        openSmartCardReader_bt = findViewById(R.id.open_smartcard_btn);
        coldReset_bt = findViewById(R.id.cold_reset_btn);
        warmReset_bt = findViewById(R.id.warm_reset_btn);
        getAtr_bt = findViewById(R.id.get_atr_btn);
        sendApdu_bt = findViewById(R.id.transmit_apdu_btn);
        closeReader_bt = findViewById(R.id.close_reader_btn);
    }

    public void onClickOpenSamReader(View v){
        if (!selectedSmartcard) {
            selectedSam = true;
            cardPowerOn = false;
            enableButtons();

            openReader();
        }
    }

    public void onClickOpenSmartcardReader(View v){
        if (!selectedSam) {
            selectedSmartcard = true;
            cardPowerOn = false;
            enableButtons();

            openReader();
        }
    }

    public void openReader(){
        if (sclib == null){
            Log.d(TAG, "sclib is null");
            if (selectedSam)
                sclib = sclibSam;
            if (selectedSmartcard)
                sclib = sclibSmartcard;
        }

        if (!readerOpen) {
            readerOpen = sclib.open();

            if (readerOpen) {
                if (selectedSam) {
                    showStringOnConsole("SAM reader opened");
                } else if (selectedSmartcard) {
                    showStringOnConsole("Smartcard reader opened");

                    cardInserted = checkSmartCardPresence();
                    sclib.registerSlotStatusCallback(this);
                }
            } else {
                Log.e(TAG, "ERROR opening reader");
                Toast.makeText(getApplicationContext(), "Issue opening reader", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "Reader is already open", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickColdReset(View v){
        if (selectedSam || selectedSmartcard) {
            if (sclib != null) {
                if (!sclib.powerOffCard()) {
                    Log.e(TAG, "ERROR powering off card");
                    Toast.makeText(getApplicationContext(), "Error powering OFF card", Toast.LENGTH_SHORT).show();
                } else {
                    powerOnCard(COLD_RESET);
                }
            } else{
                Log.e(TAG, "sclib is null");
            }
        }
    }

    public void onClickWarmReset(View v){
         if (selectedSam || selectedSmartcard) {
             powerOnCard(WARM_RESET);
        }
    }

    public void onClickGetAtr(View v){
        if (selectedSam || selectedSmartcard) {
            powerOnCard(GET_ATR);
        }
    }

    public void onClickTransmitApdu(View v){
        if ((selectedSam || selectedSmartcard) && readerOpen) {
            String apduString = apduToSend_et.getText().toString().trim();
            apduString = apduString.replace(" ", "");

            if (apduString.length() > 0) {
                if (!cardPowerOn) {
                    powerOnCard(GET_ATR);
                }

                Log.d(TAG, "APDU to send: " + apduString);
                showStringOnConsole("Sending APDU: " + apduString);

                if (sclib != null) {
                    byte[] data = sclib.sendApdu(Utility.convertHexToBytes(apduString));
//                    byte[] data = sclib.sendApdu(Utility.convertHexToBytes(apduString),
//                            SmartCardLibrary.READER_DEFAULT_TIMEOUT,
//                            SmartCardLibrary.READER_CARD_DEFAULT_BLOCK_WAITING_TIME_MULTIPLIER);
                    if (data != null) {
                        showStringOnConsole("Answer: " + Utility.convertBytesToHex(data));
                        Log.d(TAG, "Answer: " + Utility.convertBytesToHex(data));
                    } else{
                        Log.e(TAG, "Received data is null - Error: " + sclib.getLastReaderError());
                        Toast.makeText(getApplicationContext(), "Received data is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                showStringOnConsole("Please enter APDU");
            }
        }
        else{
            Log.e(TAG, "Need to open reader before sending APDUs!");
            Toast.makeText(getApplicationContext(), "Reader is not opened", Toast.LENGTH_SHORT).show();
        }
    }

    private void powerOnCard(int action){
        if (sclib != null) {
            if (readerOpen) {
                String answer = Utility.convertBytesToHex(sclib.powerOnCard());
//                String answer = Utility.convertBytesToHex(sclib.powerOnCard(sclib.READER_VOLTAGE_AUTOMATIC));
                if (answer.isEmpty()) {
                    Log.e(TAG, "Card power on failed");
                    Toast.makeText(getApplicationContext(), "Error powering ON card", Toast.LENGTH_SHORT).show();

                    if (selectedSmartcard) {
                        cardInserted = checkSmartCardPresence();

                        if (cardInserted)
                            showStringOnConsole("Card is unresponsive");
                    }
                } else {
                    Log.d(TAG, "ATR : " + answer);
                    cardPowerOn = true;
                    if (action == GET_ATR) {
                        showStringOnConsole("ATR: " + answer);
                    } else{
                        showStringOnConsole("Card powered on");
                    }
                }
            } else{
                Log.e(TAG, "Need to open reader before powering card!");
                Toast.makeText(getApplicationContext(), "Reader is not opened", Toast.LENGTH_SHORT).show();
            }
        } else{
            Log.e(TAG, "sclib is null");
        }
    }

    public void onClickCloseReader(View v){
        closeReader();

        selectedSam = false;
        selectedSmartcard = false;
        disableButtons();
    }

    private void closeReader(){
        if (sclib != null){
            if (!sclib.powerOffCard()) {
                Log.e(TAG, "Error powering off");
            }
            if (sclib.close()){
                sclib = null;
                readerOpen = false;
                cardPowerOn = false;

                if (selectedSam) {
                    showStringOnConsole("SAM reader closed");
                } else if (selectedSmartcard){
                    showStringOnConsole("Smartcard reader closed");
                }
            } else{
                Log.e(TAG, "Error closing reader");
                Toast.makeText(getApplicationContext(), "Error closing reader", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSlotStatusChanged(boolean slotStatus) {
        if (slotStatus) {
            showStringOnConsole("Card is present");
            cardInserted = true;
        }
        else {
            showStringOnConsole("Card is missing");
            cardInserted = false;
        }
    }

    public boolean checkSmartCardPresence() {
        Log.d(TAG, "checkSmartCardPresence");
        if (sclib != null) {
            if (readerOpen && selectedSmartcard) {
                int slotEmpty = sclib.isSlotEmpty();

                if (slotEmpty == 2){
                    Log.d(TAG, "Feature not available with SAM card reader");
                } else if (slotEmpty == 1){
                    Log.d(TAG, "Slot is empty");
                    showStringOnConsole("Card is missing");
                } else if (slotEmpty == 0){
                    Log.d(TAG, "Slot is not empty");
                    showStringOnConsole("Card is present");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (selectedSam || selectedSmartcard){
            closeReader();

            selectedSam = false;
            selectedSmartcard = false;
        }

        sclib = null;
        disableButtons();
    }

    public void enableButtons(){
        coldReset_bt.setEnabled(true);
        warmReset_bt.setEnabled(true);
        getAtr_bt.setEnabled(true);
        sendApdu_bt.setEnabled(true);
        closeReader_bt.setEnabled(true);

        if (selectedSam){
            openSamReader_bt.setSelected(true);
            openSmartCardReader_bt.setEnabled(false);
        }
        else if (selectedSmartcard){
            openSmartCardReader_bt.setSelected(true);
            openSamReader_bt.setEnabled(false);
        }
    }

    public void disableButtons(){
        coldReset_bt.setEnabled(false);
        warmReset_bt.setEnabled(false);
        getAtr_bt.setEnabled(false);
        sendApdu_bt.setEnabled(false);
        closeReader_bt.setEnabled(false);

        openSamReader_bt.setEnabled(true);
        openSamReader_bt.setSelected(false);
        openSmartCardReader_bt.setEnabled(true);
        openSmartCardReader_bt.setSelected(false);
    }

    public void showBytesOnConsole(byte[] data){
        if (data != null){
            if (data.length == 0) {
                Log.e(TAG, "Received data is empty");
                return;
            }

            String msg = Utility.convertBytesToHex(data);
            Log.d(TAG, "Received data: " + msg);

            apdu_tv.append("> " + msg + "\n");
            updateScrollingView();
        }
        else{
            Log.e(TAG, "Received data is null");
        }
    }

    public void showStringOnConsole(String msg){
        if (msg != null){
            if (msg.length() != 0){
                apdu_tv.append("> " + msg + "\n");
                updateScrollingView();
            }
        }
    }

    private void updateScrollingView(){
        final Layout layout = apdu_tv.getLayout();
        if(layout != null){
            int scrollDelta = layout.getLineBottom(apdu_tv.getLineCount() - 1)
                    - apdu_tv.getScrollY() - apdu_tv.getHeight();
            if(scrollDelta > 0)
                apdu_tv.scrollBy(0, scrollDelta);
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
