package com.example.healthtracker.service.viewmodels;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.healthtracker.falldetection.Accelerometer;
import com.example.healthtracker.model.LifeSensor;
import com.example.healthtracker.model.User;
import com.example.healthtracker.service.htppservices.HealthTrackerHttpServices;
import com.example.healthtracker.service.locationservice.LocationTrackingService;
import com.example.healthtracker.service.locationservice.SendLocationToActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

public class MainViewModel extends AndroidViewModel {

    private Application application;

    @Getter
    @Setter
    private User user = User.getInstance();

    //---- Location Tracking -----
    private LocationTrackingService locationTrackingService = null;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationTrackingService.LocalBinder binder = (LocationTrackingService.LocalBinder) iBinder;
            locationTrackingService = binder.getService();
            locationTrackingService.requestLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationTrackingService = null;
            locationTrackingService.removeLocationUpdates();
        }
    };
    // -------------------------

    //---- Fall Detection -----
    private final SensorManager mSensorManager;
    private Sensor mSensor;
    private final Accelerometer accelerometer;

    //---------------------------------------------------------------

    //---- Data Sending -----
    private String REQUEST_URL = "https://lg.perf.group/sensors/";
    private final int SENDING_DATA_INTERVAL = 5000;
    private Handler handler = new Handler();
    private Runnable sendingData = new Runnable() {
        @Override
        public void run() {
            packAndSendData();          // this method will contain your almost-finished HTTP calls
            handler.postDelayed(this, SENDING_DATA_INTERVAL);
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        mSensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = new Accelerometer(mSensorManager, mSensor);
    }

    public void startFallDetection() {
        accelerometer.startListening();
    }

    public void startLocationTracking() {
        application.bindService(new Intent(application.getBaseContext(), LocationTrackingService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
    }

    public void stopLocationTracking() {
        application.unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) {
        if (event != null) {
            user.setLatitude(event.getLocation().getLatitude());
            user.setLongtitude(event.getLocation().getLongitude());
        }
    }

    private void packAndSendData() {
        List<LifeSensor> lifeSensorList = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf, sdfDisplay;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdfDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateToDisplay = sdfDisplay.format(date);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = sdf.format(date);
        user.setLastSendSignal(dateToDisplay);
        timestamp += "+00:00";

        LifeSensor location = new LifeSensor("location",
                new double[]{User.getInstance().getLatitude().getValue(), User.getInstance().getLongtitude().getValue()},
                timestamp);
        LifeSensor fall = new LifeSensor("fall", User.getInstance().getFallen().getValue(), timestamp);
        LifeSensor sos = new LifeSensor("sos", User.getInstance().isSosState(), timestamp);

        lifeSensorList.add(location);
        lifeSensorList.add(fall);
        lifeSensorList.add(sos);

        Gson pojoToJSON = new Gson();
        String json = pojoToJSON.toJson(lifeSensorList);

        HealthTrackerHttpServices httpServices = new HealthTrackerHttpServices();
        String url = REQUEST_URL + User.getInstance().getDeviceId().getValue();
        httpServices.postRequst(url, json);
    }

    public void scheduleSendData() {
        handler.postDelayed(sendingData, SENDING_DATA_INTERVAL);
    }

    public void unscheduleSendData() {
        handler.removeCallbacks(sendingData);
    }
}
