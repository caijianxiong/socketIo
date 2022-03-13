package com.example.socketdemo.been;

public class ResponseBeen {
    private String uuid;
    private String time;
    private String code;
    private String msg;
    private Results results;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    private class Results {
        private long time_in_nano;

        public long getTime_in_nano() {
            return time_in_nano;
        }

        public void setTime_in_nano(long time_in_nano) {
            this.time_in_nano = time_in_nano;
        }
    }
}
