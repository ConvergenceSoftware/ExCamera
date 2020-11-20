package com.serenegiant.usb.entity;

public class SupportEntity {

    private String des;
    private boolean enable;

    public SupportEntity(String des, boolean enable) {
        this.des = des;
        this.enable = enable;
    }

    public String getDes() {
        return des;
    }

    public boolean isEnable() {
        return enable;
    }

    @Override
    public String toString() {
        return des + " : " + (enable ? "Enable" : "Disable");
    }
}
