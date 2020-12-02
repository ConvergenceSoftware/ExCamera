package com.convergence.excamera.sdk.wifi.entity;

import android.text.TextUtils;

import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * WiFi相机分辨率封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraResolution {

    /**
     * resolutionList = [<8>1024 * 768, <7>1280 * 720, <6>1600 * 1200, <0>1920 * 1080, <5>2048 * 1536,
     * <4>2592 * 1944, <3>3264 * 2448, <2>3840 * 2160, <1>3840 * 3104] ,
     * curResolution = <7>1280 * 720
     */

    /**
     * 分辨率列表
     */
    private List<Resolution> resolutionList;
    /**
     * 当前分辨率
     */
    private Resolution curResolution;
    /**
     * 是否可用
     */
    private boolean isAvailable = false;

    public WifiCameraResolution(NConfigList.FormatsBean formatsBean) {
        resolutionList = new ArrayList<>();
        Map<Integer, String> resolutionDesMap = formatsBean.getResolutions();
        if (resolutionDesMap == null || resolutionDesMap.isEmpty()) {
            return;
        }
        for (int i = 0; i < resolutionDesMap.size(); i++) {
            String resolutionDes = resolutionDesMap.get(i);
            if (resolutionDes == null || TextUtils.isEmpty(resolutionDes)) {
                continue;
            }
            Resolution resolution = new Resolution(resolutionDes, i);
            resolutionList.add(resolution);
        }
        if (resolutionList.isEmpty()) {
            return;
        }
        Collections.sort(resolutionList, (o1, o2) -> {
            if (o1.getWidth() == o2.getWidth()) {
                return o1.getHeight() - o2.getHeight();
            } else {
                return o1.getWidth() - o2.getWidth();
            }
        });
        for (int i = 0; i < resolutionList.size(); i++) {
            Resolution resolution = resolutionList.get(i);
            if (formatsBean.getCurrentResolution() == resolution.getPosition()) {
                curResolution = resolution;
                break;
            }
        }
        if (curResolution != null) {
            isAvailable = true;
        }
    }

    public List<Resolution> getResolutionList() {
        return resolutionList;
    }

    public void setCurResolution(Resolution curResolution) {
        this.curResolution = curResolution;
    }

    public Resolution getCurResolution() {
        return curResolution;
    }

    public Resolution findResolution(int width, int height) {
        for (int i = 0; i < resolutionList.size(); i++) {
            Resolution resolution = resolutionList.get(i);
            if (width == resolution.getWidth() && height == resolution.getHeight()) {
                return resolution;
            }
        }
        return null;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return "WifiCameraResolution : resolutionList = " + Arrays.toString(resolutionList.toArray()) +
                " , curResolution = " + getCurResolution().toString();
    }

    /**
     * 分辨率实体类，由WiFi相机参数中的分辨率解析得到
     */
    public static class Resolution {

        private int position;
        private int width;
        private int height;

        public Resolution(String des, int position) {
            this.position = position;
            String[] data = des.split("x");
            width = Integer.parseInt(data[0]);
            height = Integer.parseInt(data[1]);
        }

        public int getPosition() {
            return position;
        }

        public String getDes() {
            return width + " * " + height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isDefault() {
            return width == WifiCameraConstant.DEFAULT_RESOLUTION_WIDTH && height == WifiCameraConstant.DEFAULT_RESOLUTION_HEIGHT;
        }

        @Override
        public String toString() {
            return "<" + position + ">" + getDes();
        }
    }
}
