package com.convergence.excamera.sdk.usb.entity;

import android.content.Context;
import android.content.SharedPreferences;

import com.convergence.excamera.sdk.usb.UsbCameraConstant;
import com.convergence.excamera.sdk.common.OutputUtil;

/**
 * USB相机SharePreferences封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraSP {

    private static final String SP_NAME_USB_CAMERA_SETTING = "usb_camera_config";

    private static final String SP_KEY_USB_CAMERA_OUTPUT_ROOT_PATH = "cameraOutputRootPath";
    private static final String SP_KEY_USB_CAMERA_IS_FLIP_HORIZONTAL = "isFlipHorizontal";
    private static final String SP_KEY_USB_CAMERA_IS_FLIP_VERTICAL = "isFlipVertical";

    private UsbCameraSP() {

    }

    public static Editor getEditor(Context context) {
        return new Editor(context);
    }

    public static class Editor {

        private SharedPreferences sp;

        private Editor(Context context) {
            sp = context.getSharedPreferences(SP_NAME_USB_CAMERA_SETTING, Context.MODE_PRIVATE);
        }

        private void putString(String key, String content) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, content);
            editor.apply();
        }

        private void putBoolean(String key, boolean content) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, content);
            editor.apply();
        }

        private void putFloat(String key, float content) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(key, content);
            editor.apply();
        }

        private void putInt(String key, int content) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, content);
            editor.apply();
        }

        //相机输出图片、视频保存目录
        public void setCameraOutputRootPath(String value) {
            putString(SP_KEY_USB_CAMERA_OUTPUT_ROOT_PATH, value);
        }

        public String getCameraOutputRootPath() {
            return sp.getString(SP_KEY_USB_CAMERA_OUTPUT_ROOT_PATH, OutputUtil.getDefaultRootPath());
        }

        //是否水平翻转
        public void setIsFlipHorizontal(boolean value) {
            putBoolean(SP_KEY_USB_CAMERA_IS_FLIP_HORIZONTAL, value);
        }

        public boolean isFlipHorizontal() {
            return sp.getBoolean(SP_KEY_USB_CAMERA_IS_FLIP_HORIZONTAL, UsbCameraConstant.DEFAULT_IS_FLIP_HORIZONTAL);
        }

        //是否垂直翻转
        public void setIsFlipVertical(boolean value) {
            putBoolean(SP_KEY_USB_CAMERA_IS_FLIP_VERTICAL, value);
        }

        public boolean isFlipVertical() {
            return sp.getBoolean(SP_KEY_USB_CAMERA_IS_FLIP_VERTICAL, UsbCameraConstant.DEFAULT_IS_FLIP_VERTICAL);
        }
    }
}
