package com.serenegiant.usb.entity;

import java.util.ArrayList;
import java.util.List;

public class ControlSupports {

    private static final String[] SUPPORTS_CTRL = {
            "D0:  Scanning Mode",
            "D1:  Auto-Exposure Mode",
            "D2:  Auto-Exposure Priority",
            "D3:  Exposure Time (Absolute)",
            "D4:  Exposure Time (Relative)",
            "D5:  Focus (Absolute)",
            "D6:  Focus (Relative)",
            "D7:  Iris (Absolute)",
            "D8:  Iris (Relative)",
            "D9:  Zoom (Absolute)",
            "D10: Zoom (Relative)",
            "D11: PanTilt (Absolute)",
            "D12: PanTilt (Relative)",
            "D13: Roll (Absolute)",
            "D14: Roll (Relative)",
            "D15: Reserved",
            "D16: Reserved",
            "D17: Focus, Auto",
            "D18: Privacy",
            "D19: Focus, Simple",
            "D20: Window",
            "D21: Region of Interest",
            "D22: Reserved, set to zero",
            "D23: Reserved, set to zero",
    };

    private long supports = 0L;
    private List<SupportEntity> supportEntityList;

    public ControlSupports() {
        supportEntityList = new ArrayList<>();
    }

    public void refresh(long supports) {
        this.supports = supports;
        supportEntityList.clear();
        for (int i = 0; i < SUPPORTS_CTRL.length; i++) {
            supportEntityList.add(new SupportEntity(SUPPORTS_CTRL[i], (supports & (0x1 << i)) != 0));
        }
    }

    public boolean isAvailable() {
        return supports != 0L && !supportEntityList.isEmpty();
    }

    public long getSupports() {
        return supports;
    }

    public List<SupportEntity> getSupportEntityList() {
        return supportEntityList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ControlSupports = ").append(supports);
        if (isAvailable()) {
            for (int i = 0; i < supportEntityList.size(); i++) {
                stringBuilder.append("\n").append(supportEntityList.get(i).toString());
            }
        }
        return stringBuilder.toString();
    }
}
