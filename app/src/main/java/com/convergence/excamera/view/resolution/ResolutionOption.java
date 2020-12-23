package com.convergence.excamera.view.resolution;

import com.convergence.excamera.sdk.usb.entity.UsbCameraResolution;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraResolution;

/**
 * 分辨率选项
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class ResolutionOption {

    private int width;
    private int height;
    private boolean isDefault = false;
    private boolean isSelect = false;

    public ResolutionOption(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean equals(int width, int height) {
        return this.width == width && this.height == height;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public String getDes() {
        return isDefault ? toString() + " : Default" : toString();
    }

    @Override
    public String toString() {
        return width + " * " + height;
    }
}
