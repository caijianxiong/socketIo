package com.example.socketdemo.been;

public class VolumeRequest extends BaseRequestBeen {

    private Parameters parameters;

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public static class Parameters extends BaseParameters {
        public Parameters(int volume) {
            this.volume = volume;
        }

        public int volume;

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }
    }
}
