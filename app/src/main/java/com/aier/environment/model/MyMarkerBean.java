package com.aier.environment.model;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class MyMarkerBean implements Serializable {
    public double latitude;
    public double longitude;
    public String name;
    public List<LatLng> latLngs_list;
}
