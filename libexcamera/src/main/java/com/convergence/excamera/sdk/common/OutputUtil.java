package com.convergence.excamera.sdk.common;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Size;

import androidx.core.math.MathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 相机输出图片、视频工具类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class OutputUtil {

    private static final int RECORD_BIT_RATE_MIN = 2 * 1024 * 1024;
    private static final int RECORD_BIT_RATE_MAX = 50 * 1024 * 1024;

    /**
     * 获取默认存储路径
     */
    public static String getDefaultRootPath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return sdcardPath + File.separator + "ExCamera";
    }

    /**
     * 获取随机的图片文件名
     */
    private static String getRandomPicName() {
        return getNowDateForPic() + (int) ((Math.random() * 9 + 1) * 100000) + ".png";
    }

    /**
     * 获取随机的视频文件名
     */
    private static String getRandomVideoName() {
        return getNowDateForPic() + (int) ((Math.random() * 9 + 1) * 100000) + ".mp4";
    }

    /**
     * 获取随机的图片保存路径
     */
    public static String getRandomPicPath() {
        return getRandomPicPath(getDefaultRootPath());
    }

    /**
     * 获取随机的视频保存路径
     */
    public static String getRandomVideoPath() {
        return getDefaultRootPath() + File.separator + getRandomVideoName();
    }

    /**
     * 获取随机的图片保存路径
     *
     * @param rootPath 图片保存目录
     */
    public static String getRandomPicPath(String rootPath) {
        if (TextUtils.isEmpty(rootPath)) {
            return getDefaultRootPath() + File.separator + getRandomPicName();
        } else {
            return rootPath + File.separator + getRandomPicName();
        }
    }

    /**
     * 获取随机的视频保存路径
     *
     * @param rootPath 视频保存目录
     */
    public static String getRandomVideoPath(String rootPath) {
        if (TextUtils.isEmpty(rootPath)) {
            return getDefaultRootPath() + File.separator + getRandomVideoName();
        } else {
            return rootPath + File.separator + getRandomVideoName();
        }
    }

    /**
     * 从指定的屏幕旋转中获取输出方向
     *
     * @param rotation 屏幕方向
     * @return 输出方向（0,90,270,360）
     */
    public static int getOutputOrientation(int rotation) {
        return rotation == 0 || rotation == 180 ? rotation + 90 : rotation - 90;
    }

    /**
     * 保存文件
     *
     * @param pathToSave 保存文件的本地路径，包括文件名（绝对路径）
     * @param data       二进制byte数组
     */
    public static void saveFile(String pathToSave, byte[] data) throws Exception {
        File file = new File(pathToSave);
        File parentFile = new File(file.getParent());
        if (file.exists()) {
            file.delete();
        } else if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

    /**
     * 获取现在时间
     */
    private static String getNowDateForPic() {
        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(currentTime);
    }

    /**
     * 获取即时帧率文本
     */
    public static String getInstantFPSText(int fps) {
        return String.format("FPS : %2d", fps);
    }

    /**
     * 获取平均帧率文本
     */
    public static String getAverageFPSText(float fps) {
        return String.format("FPS : %2.1f", fps);
    }

    /**
     * 根据录制秒数获取“00:00”格式文本
     */
    public static String getRecordTimeText(int recordSeconds) {
        StringBuilder stringBuilder = new StringBuilder();
        int minute = recordSeconds / 60;
        int second = recordSeconds % 60;
        if (minute < 10) {
            stringBuilder.append("0").append(minute).append(":");
        } else {
            stringBuilder.append(minute).append(":");
        }
        if (second < 10) {
            stringBuilder.append("0").append(second);
        } else {
            stringBuilder.append(second);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据录制秒数获取“00:00:00”格式文本
     */
    public static String getLongRecordTimeText(int recordSeconds) {
        StringBuilder stringBuilder = new StringBuilder();
        int hour = recordSeconds / 3600;
        int minute = recordSeconds % 3600 / 60;
        int second = recordSeconds % 3600 % 60;
        if (hour < 10) {
            stringBuilder.append("0").append(hour).append(":");
        } else {
            stringBuilder.append(hour).append(":");
        }
        if (minute < 10) {
            stringBuilder.append("0").append(minute).append(":");
        } else {
            stringBuilder.append(minute).append(":");
        }
        if (second < 10) {
            stringBuilder.append("0").append(second);
        } else {
            stringBuilder.append(second);
        }
        return stringBuilder.toString();
    }

    /**
     * 计算比特率
     *
     * @param size  视频分辨率
     * @param frame 视频帧率
     * @return 合适大小的比特率
     */
    public static int calculateBitRate(Size size, int frame) {
        int bitRate = size.getWidth() * size.getHeight() * frame / 8;
        return MathUtils.clamp(bitRate, RECORD_BIT_RATE_MIN, RECORD_BIT_RATE_MAX);
    }
}
