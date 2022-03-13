package com.example.socketdemo.been;

public class DiscoverResponse {
    private int code;
    private String msg;
    private String uuid;
    private long time;
    private String sn;
    private boolean connectable;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public boolean isConnectable() {
        return connectable;
    }

    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }
}
