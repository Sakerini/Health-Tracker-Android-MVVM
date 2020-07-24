package com.example.healthtracker.service.viewmodels;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.healthtracker.model.User;
import com.example.healthtracker.service.htppservices.HealthTrackerHttpServices;
import com.example.healthtracker.view.ui.MainActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StarterViewModel extends AndroidViewModel {

    private String REQUEST_URL = "https://lg.perf.group/sensors/";
    private boolean permissionStatus = false;

    private MutableLiveData<User> userObservable = new MutableLiveData<>();

    private Application application;

    View.OnLongClickListener clickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            holdButtonLabelVisibleObservable.setValue(false);
            buttonVisibleObservable.setValue(false);
            connectingLabelVisibleObservable.setValue(true);
            spinnerVisibleObservable.setValue(true);
            requsetAllPermissions();
            if (permissionStatus == true)
                connectToServer(view);
            else {
                holdButtonLabelVisibleObservable.setValue(true);
                buttonVisibleObservable.setValue(true);
                connectingLabelVisibleObservable.setValue(false);
                spinnerVisibleObservable.setValue(false);
            }
            return false;
        }
    };

    private MutableLiveData<Boolean> buttonVisibleObservable = new MutableLiveData<>();
    private MutableLiveData<Boolean> holdButtonLabelVisibleObservable = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectingLabelVisibleObservable = new MutableLiveData<>();
    private MutableLiveData<Boolean> spinnerVisibleObservable = new MutableLiveData<>();

    public StarterViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userObservable.setValue(User.getInstance());
        holdButtonLabelVisibleObservable.setValue(true);
        buttonVisibleObservable.setValue(true);
        connectingLabelVisibleObservable.setValue(false);
        spinnerVisibleObservable.setValue(false);
    }

    public void getUserInformation() {
        userObservable.getValue().setConnected(false);
        String id = Settings.Secure.getString(application.getContentResolver(), Settings.Secure.ANDROID_ID);
        userObservable.getValue().setDeviceId(id);
        userObservable.getValue().setLatitude(0.0);
        userObservable.getValue().setLongtitude(0.0);
        userObservable.getValue().setLastSendSignal("None");
        userObservable.getValue().setSosState(false);
        userObservable.getValue().setFallen(false);
    }

    public void connectToServer(final View view) {

        HealthTrackerHttpServices healthTrackerHttpServices = new HealthTrackerHttpServices();
        String jsonResponseBody = null;
        try {
            jsonResponseBody = healthTrackerHttpServices.getRequest(REQUEST_URL
                    + User.getInstance().getDeviceId().getValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (jsonResponseBody != null && (jsonResponseBody.contains("not found") || jsonResponseBody.contains("false"))) {
            HealthTrackerHttpServices httpServices = new HealthTrackerHttpServices();
            String json = "{}";
            String url = REQUEST_URL + User.getInstance().getDeviceId().getValue();
            httpServices.putRequest(url,json); // HERE IT SHOULD CALL A PUT REQUEST BECAUSE WE
            // CHANGED STATUS SO THAT IT CHANGES ON SERVER TOO
        }
        User.getInstance().setConnected(true);

        Context context = view.getContext();
        Intent intent = new Intent(context, MainActivity.class);
        holdButtonLabelVisibleObservable.setValue(true);
        buttonVisibleObservable.setValue(true);
        connectingLabelVisibleObservable.setValue(false);
        spinnerVisibleObservable.setValue(false);
        context.startActivity(intent);
    }

    private void requsetAllPermissions() {
        Dexter.withContext(application.getBaseContext()).withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    permissionStatus = true;
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // permission is denied permenantly, navigate user to app settings
                    report.getDeniedPermissionResponses();
                    openSettings();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();
    }

    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", application.getPackageName(), null);
        intent.setData(uri);
        application.startActivity(intent);
    }

    public MutableLiveData<User> getUserObservable() {
        return userObservable;
    }

}
