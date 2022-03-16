package com.idemia.idscreen.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.idemia.idscreen.CameraLedActivity;
import com.idemia.idscreen.FeatureDetailActivity;
import com.idemia.idscreen.FpSensorActivity;
import com.idemia.idscreen.R;
import com.idemia.idscreen.SmartCardReaderActivity;
import com.idemia.idscreen.UsbManagementActivity;

import java.util.ArrayList;

public class HomeScreenItemsAdapter extends RecyclerView.Adapter<HomeScreenItemsAdapter.ItemHolder> {
    Context activityContext;
    ArrayList featureList;

    public HomeScreenItemsAdapter(Context context, ArrayList featureList) {
        this.activityContext = context;
        this.featureList = featureList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_items, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        setItems(position, holder);
    }

    private void setItems(int position, ItemHolder holder) {
        switch (position) {
            case 0:
                holder.itemName.setText(featureList.get(position).toString());
                holder.itemLogo.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.fingerprint));
                break;
            case 1:
                holder.itemName.setText(featureList.get(position).toString());
                holder.itemLogo.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.usb));
                break;
            case 2:
                holder.itemName.setText(featureList.get(position).toString());
                holder.itemLogo.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.led));
                break;
            case 3:
                holder.itemName.setText(featureList.get(position).toString());
                holder.itemLogo.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.credit_card));
                break;
            case 4:
                holder.itemName.setText(featureList.get(position).toString());
                holder.itemLogo.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.eye));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        ImageView itemLogo;
        AppCompatTextView itemName;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            itemLogo = itemView.findViewById(R.id.item_logo);
            itemName = itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if (itemName.getText().toString().equalsIgnoreCase(activityContext.getResources().getString(R.string.fp_sample))) {
                        intent = new Intent(activityContext, FpSensorActivity.class);
                    } else if (itemName.getText().toString().equalsIgnoreCase(activityContext.getResources().getString(R.string.led_sample))) {
                        intent = new Intent(activityContext, CameraLedActivity.class);
                    } else if (itemName.getText().toString().equalsIgnoreCase(activityContext.getResources().getString(R.string.usb_sample))) {
                        intent = new Intent(activityContext, UsbManagementActivity.class);
                    } else if (itemName.getText().toString().equalsIgnoreCase(activityContext.getResources().getString(R.string.contact_card_sample))) {
                        intent = new Intent(activityContext, SmartCardReaderActivity.class);
                    } else {
                        intent = new Intent(activityContext, FeatureDetailActivity.class);
                    }
                    // intent  =new Intent(activityContext, FeatureDetailActivity.class);
                    intent.putExtra("TITLE", itemName.getText().toString());
                    activityContext.startActivity(intent);
                }
            });
        }
    }
}
