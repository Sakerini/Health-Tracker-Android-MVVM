package com.example.healthtracker.view.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.healthtracker.R;

public class MainBindingAdapters {


    @BindingAdapter("app:signal")
    public static void updateLatitude(TextView tv_last_signal, String date) {
        tv_last_signal.setText(tv_last_signal.getResources().getString(R.string.last_data_transmit, date));
    }

    @BindingAdapter("app:longtitude")
    public static void updateLongtitude(TextView tv_longtitude, double longtitude) {
        tv_longtitude.setText(tv_longtitude.getResources().getString(R.string.your_longtitude, String.valueOf(longtitude)));
    }

    @BindingAdapter("app:connect_status")
    public static void statusDisplay(TextView tv_connect_status, boolean status) { //todo connect v resursax
        if (status) {
            //COLORING
            SpannableString ss = new SpannableString(tv_connect_status.getResources().getString(R.string.status,
                    tv_connect_status.getResources().getString(R.string.connected)));
            ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.GREEN);
            ss.setSpan(fcsGreen, 8, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_connect_status.setText(ss);
        } else {
            //COLORING
            SpannableString ss = new SpannableString(tv_connect_status.getResources().getString(R.string.status,
                    tv_connect_status.getResources().getString(R.string.disconnected)));
            ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.RED);
            ss.setSpan(fcsGreen, 8, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_connect_status.setText(ss);
        }
    }

    @BindingAdapter("app:latitude")
    public static void updateLatitude(TextView tv_latitude, double latitude) {
        tv_latitude.setText(tv_latitude.getResources().getString(R.string.your_latitude, String.valueOf(latitude)));
    }

    @BindingAdapter("app:deviceId")
    public static void updateDeviceId(TextView tv_device_id, String id) {
        tv_device_id.setText(tv_device_id.getResources().getString(R.string.deviceIDSTRING, id));
    }

    @BindingAdapter("app:visibility")
    public static void setButtonVisibility(View view, boolean isVisible) {
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
