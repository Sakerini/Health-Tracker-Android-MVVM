package com.example.healthtracker.view.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthtracker.R;
import com.example.healthtracker.databinding.MainActivityBinding;
import com.example.healthtracker.service.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    public MainViewModel viewModel;
    private MainActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        startFallDetection();
        startTracking();
        scheduleSendSensorsData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationSettings();
    }

    private void checkLocationSettings() {
        LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGpsDialog();
        }
    }

    private void enableGpsDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        alert.show();
    }

    public void scheduleSendSensorsData() {
        viewModel.scheduleSendData();
    }

    public void unscheduleSendSensorsData() {
        viewModel.unscheduleSendData();
    }

    public void startFallDetection() {
        viewModel.startFallDetection();
    }

    public void startTracking() {
        viewModel.startLocationTracking();
    }

    public void stopTracking() {
        viewModel.stopLocationTracking();
    }
}