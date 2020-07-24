package com.example.healthtracker.model;

import lombok.Data;

@Data
public class LifeSensor {
    public String sensorType;
    public Object currentValue;
    public String timeStamp;

    public LifeSensor(String sensorType, Object currentValue, String date) {
        this.sensorType = sensorType;
        this.currentValue = currentValue;
        this.timeStamp = date;
    }
}