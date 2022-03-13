package com.example.socketdemo.been;

public class BaseRequestBeen {
    private String name;
    private String uuid;
    private long time;
    private BaseParameters baseParameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public BaseParameters getBaseParameters() {
        return baseParameters;
    }

    public void setBaseParameters(BaseParameters baseParameters) {
        this.baseParameters = baseParameters;
    }
}
