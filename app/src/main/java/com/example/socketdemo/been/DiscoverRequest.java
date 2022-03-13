package com.example.socketdemo.been;

public class DiscoverRequest {
    private String name;
    private String uuid;
    private long time;
    private Object parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }
}
