package de.lpelster.devicesimulation.entity;

import java.net.http.WebSocket;

public class SimulatedDevice {

    private long id;
    private final String name;
    private int intervalSeconds;
    private long lastSent;
    private WebSocket socket;
    private boolean connected;

    public SimulatedDevice(String name, int intervalSeconds) {
        this.name = name;
        this.intervalSeconds = intervalSeconds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int seconds){
        this.intervalSeconds = seconds;
    }

    public long getLastSent() {
        return lastSent;
    }

    public void setLastSent(long lastSent) {
        this.lastSent = lastSent;
    }

    public WebSocket getSocket() {
        return socket;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}