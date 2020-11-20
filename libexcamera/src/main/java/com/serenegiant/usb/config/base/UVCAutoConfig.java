package com.serenegiant.usb.config.base;

public abstract class UVCAutoConfig extends UVCConfig {

    protected boolean def;
    protected boolean isAuto;
    private boolean isInit = false;

    protected UVCAutoConfig(String tag) {
        super(tag);
    }

    public boolean getDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
        if (!isInit) {
            isAuto = def;
            isInit = true;
        }
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    @Override
    public String toString() {
        return "Config <" + tag + "> : flag = " + flag + " , isEnable = " + isEnable + " , isAuto = " + isAuto;
    }
}
