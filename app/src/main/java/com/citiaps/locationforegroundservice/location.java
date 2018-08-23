package com.citiaps.locationforegroundservice;

import com.google.gson.annotations.SerializedName;

public class location {
    @SerializedName("id")
    private Integer id;
    @SerializedName("userID")
    private String userID;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lon")
    private Double lon;
    @SerializedName("timestamp")
    private Long timestamp;
    @SerializedName("accuracy")
    private Float accuracy;
    @SerializedName("altitude")
    private Double altitude;
    @SerializedName("speed")
    private Float speed;


//TODO seguir tutorial de https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23

    public location(Integer id, String userID, Double lat, Double lon, Long timestamp,
                    Float accuracy, Double altitude, Float speed) {
        this.id = id;
        this.userID = userID;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.speed = speed;
    }
}
