package com.serenegiant.usb.entity;

import java.util.ArrayList;
import java.util.List;

public class ProcessSupports {

    private static final String[] SUPPORTS_PROC = {
            "D0: Brightness",
            "D1: Contrast",
            "D2: Hue",
            "D3: Saturation",
            "D4: Sharpness",
            "D5: Gamma",
            "D6: White Balance Temperature",
            "D7: White Balance Component",
            "D8: Backlight Compensation",
            "D9: Gain",
            "D10: Power Line Frequency",
            "D11: Hue, Auto",
            "D12: White Balance Temperature, Auto",
            "D13: White Balance Component, Auto",
            "D14: Digital Multiplier",
            "D15: Digital Multiplier Limit",
            "D16: Analog Video Standard",
            "D17: Analog Video Lock Status",
            "D18: Contrast, Auto",
            "D19: Reserved. Set to zero",
            "D20: Reserved. Set to zero",
            "D21: Reserved. Set to zero",
            "D22: Reserved. Set to zero",
            "D23: Reserved. Set to zero",
    };

    private long supports = 0L;
    private List<SupportEntity> supportEntityList;

    public ProcessSupports() {
        supportEntityList = new ArrayList<>();
    }

    public void refresh(long supports) {
        this.supports = supports;
        supportEntityList.clear();
        for (int i = 0; i < SUPPORTS_PROC.length; i++) {
            supportEntityList.add(new SupportEntity(SUPPORTS_PROC[i], (supports & (0x1 << i)) != 0));
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
        stringBuilder.append("ProcessSupports = ").append(supports);
        if (isAvailable()) {
            for (int i = 0; i < supportEntityList.size(); i++) {
                stringBuilder.append("\n").append(supportEntityList.get(i).toString());
            }
        }
        return stringBuilder.toString();
    }
}
