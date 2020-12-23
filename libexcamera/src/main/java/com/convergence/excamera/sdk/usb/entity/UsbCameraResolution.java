package com.convergence.excamera.sdk.usb.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.convergence.excamera.sdk.usb.UsbCameraConstant;
import com.serenegiant.usb.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * USB相机分辨率封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraResolution {

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

    public UsbCameraResolution() {
        resolutionList = new ArrayList<>();
    }

    public void initData(List<Size> supportSizes) {
        resolutionList.clear();
        if (supportSizes == null || supportSizes.isEmpty()) {
            return;
        }
        List<String> sizeStrings = new ArrayList<>();
        for (int i = 0; i < supportSizes.size(); i++) {
            Resolution resolution = new Resolution(supportSizes.get(i));
            if (!sizeStrings.contains(resolution.toString())) {
                sizeStrings.add(resolution.toString());
                resolutionList.add(resolution);
            }
        }
        Collections.sort(resolutionList, (o1, o2) -> {
            if (o1.getWidth() == o2.getWidth()) {
                return o1.getHeight() - o2.getHeight();
            } else {
                return o1.getWidth() - o2.getWidth();
            }
        });
        reset();
        isAvailable = true;
    }

    public void reset() {
        for (int i = 0; i < resolutionList.size(); i++) {
            Resolution resolution = resolutionList.get(i);
            if (resolution.isDefault()) {
                curResolution = resolution;
            }
        }
        if (curResolution == null) {
            curResolution = resolutionList.get(0);
        }
    }

    public void clear() {
        resolutionList.clear();
        curResolution = null;
        isAvailable = false;
    }

    public List<Resolution> getResolutionList() {
        return resolutionList;
    }

    public Resolution getCurResolution() {
        return curResolution;
    }

    public void setCurResolution(Resolution curResolution) {
        this.curResolution = curResolution;
    }

    public boolean isAvailable() {
        return isAvailable && !resolutionList.isEmpty() && curResolution != null;
    }

    @Override
    public String toString() {
        return "UsbCameraResolution{" +
                "resolutionList=" + Arrays.toString(resolutionList.toArray()) +
                ", curResolution=" + curResolution.toString() +
                '}';
    }

    /**
     * 单个分辨率实体
     */
    public static class Resolution implements Parcelable {

        private Size size;

        public Resolution(Size size) {
            this.size = size;
        }

        public boolean isDefault() {
            return size.width == UsbCameraConstant.DEFAULT_RESOLUTION_WIDTH && size.height == UsbCameraConstant.DEFAULT_RESOLUTION_HEIGHT;
        }

        public Size getSize() {
            return size;
        }

        public int getWidth() {
            return size.width;
        }

        public int getHeight() {
            return size.height;
        }

        @Override
        public String toString() {
            return size.width + " * " + size.height;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.size, flags);
        }

        protected Resolution(Parcel in) {
            this.size = in.readParcelable(Size.class.getClassLoader());
        }

        public static final Creator<Resolution> CREATOR = new Creator<Resolution>() {
            @Override
            public Resolution createFromParcel(Parcel source) {
                return new Resolution(source);
            }

            @Override
            public Resolution[] newArray(int size) {
                return new Resolution[size];
            }
        };
    }
}
