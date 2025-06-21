package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiVenue implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("capacity")
    private int capacity;

    @SerializedName("city")
    private String city;

    public String getName() { return name; }
    public String getCity() { return city; }
    public int getCapacity() { return capacity; }
}