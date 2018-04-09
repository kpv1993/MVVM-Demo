package com.example.prashanth.porterdemo.engine;

public interface IEngine {
    void onStart();

    void getService();

    void getCost(double lat, double lng);

    void getEta(double lat, double lng);
}
