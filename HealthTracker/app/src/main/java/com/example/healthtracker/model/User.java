package com.example.healthtracker.model;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User extends BaseObservable {

    private static User instance = null;

    private final MutableLiveData<String> deviceId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> connected = new MutableLiveData<>();

    private final MutableLiveData<Double> longtitude = new MutableLiveData<>();
    private final MutableLiveData<Double> latitude = new MutableLiveData<>();
    private final MutableLiveData<String> lastSendSignal = new MutableLiveData<>();

    private final MutableLiveData<Boolean> fallen = new MutableLiveData<>();
    @Setter
    private boolean sosState;

    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public void setLatitude(double latitude) {
        this.latitude.setValue(latitude);
    }

    public void setLongtitude(double longtitude) {
        this.longtitude.setValue(longtitude);
    }

    public void setDeviceId(String deviceId) {
        this.deviceId.setValue(deviceId);
    }

    public void setConnected(boolean status) {
        this.connected.setValue(status);
    }

    public void setFallen(boolean fallenStatus) {
        this.fallen.setValue(fallenStatus);
    }


    public void setLastSendSignal(String lastSendSignal) {
        this.lastSendSignal.setValue(lastSendSignal);
    }
}
