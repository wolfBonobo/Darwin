package com.example.pedro.greateranglia.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pedro on 16/04/2018.
 */

public class Alert implements Serializable {

    private String id;
    private String alert;

    ArrayList<Double> location;

    public Alert() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }
}
