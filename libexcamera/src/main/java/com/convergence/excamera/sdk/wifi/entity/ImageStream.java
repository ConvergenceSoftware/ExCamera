package com.convergence.excamera.sdk.wifi.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.common.CameraLogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * WiFi传输图片数据流
 * 该类继承了DataInputStream实现了Serializable接口
 * 1. 实例化流,获取初始化流和关闭实例流的方法
 * 2. 一个构造函数
 * 3. 一个根据帧数据大小获得位图方法
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class ImageStream extends DataInputStream implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用UE打开发现 每一个jpg格式的图片 开始两字节都是 0xFF,0xD8
     */
    //private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    //private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
    private static final int[] SOI_MARKER = {255, 216};
    private static final int[] EOF_MARKER = {255, 217};

    private static final int HEADER_MAX_LENGTH = 100;
    private static final int FRAME_MAX_LENGTH = 1000000 + HEADER_MAX_LENGTH;
    private static final String CONTENT_LENGTH = "Content-Length";

    private static ImageStream stream = null;
    private int mContentLength = -1;
    private CameraLogger cameraLogger = WifiCameraConstant.GetLogger();

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    private ImageStream(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    /**
     * 初始化流对象
     */
    public static void initInstance(InputStream is) {
        if (stream == null) {
            stream = new ImageStream(is);
        }
    }

    /**
     * 关闭流并释放Stream流对象
     */
    public static void closeInstance() {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stream = null;
    }

    /**
     * 获得创建的Stream流对象
     */
    public static ImageStream getInstance() {
        return stream;
    }

    /**
     * 此方法功能是找到索引0xFF,0XD8在字符流的位置
     * 整个数据流形式：http头信息 帧头(0xFF 0xD8) 帧数据 帧尾(0xFF 0xD9)
     * 1、首先通过0xFF 0xD8找到帧头位置
     * 2、帧头位置前的数据就是http头，里面包含Content-Length，这个字段指示了整个帧数据的长度
     * 3、帧头位置后面的数据就是帧图像的开始位置
     */
    private int getStartOfSequence(DataInputStream in, int[] sequence)
            throws IOException {
        int end = getEndOfSequence(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    /**
     * 在数据流里面找SOI_MARKER={(byte)0xFF,(byte) 0xD8}
     * 所有对IO流的操作都会抛出异常
     */
    private int getEndOfSequence(DataInputStream in, int[] sequence) throws IOException {
        int seqIndex = 0;
        int c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    /**
     * 从http的头信息中获取Content-Length，知道一帧数据的长度
     */
    private int parseContentLength(byte[] headerBytes) throws IOException,
            NumberFormatException {
        /*
          根据字节流创建ByteArrayInputStream流
          Properties是java.util包里的一个类，它有带参数和不带参数的构造方法，表示创建无默认值和有默认值的属性列表
          根据流中的http头信息生成属性文件，然后找到属性文件CONTENT_LENGTH的value，这就找到了要获得的帧数据大小
          创建一个 ByteArrayInputStream，使用 headerBytes作为其缓冲区数组
         */
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();/*创建一个无默认值的空属性列表*/
        props.load(headerIn);/*从输入流中生成属性列表（键和元素对）。*/
        String parse = props.getProperty(CONTENT_LENGTH);   //这个位置如果错误记得看一下，注意大小写对比
        return Integer.parseInt(parse);/*用指定的键在此属性列表中搜索属性。*/
    }

    /**
     * 从流中读取一帧数据
     */
    public Bitmap readImageFrame() throws IOException {
        TimeLogger timeLogger = new TimeLogger();

        /*
         读取Header
         */
        long startTime = System.currentTimeMillis();
        timeLogger.total.setStartTime(startTime);
        timeLogger.readHeader.setStartTime(startTime);
        mark(FRAME_MAX_LENGTH);/*流中当前的标记位置*/
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();/*将缓冲区的位置重置为标记位置*/
        byte[] header = new byte[headerLen];
        readFully(header);/*会一直阻塞等待，直到数据全部到达(数据缓冲区装满)*/
        try {
            mContentLength = parseContentLength(header);
        } catch (NumberFormatException nfe) {
            mContentLength = getEndOfSequence(this, EOF_MARKER);
        }
        timeLogger.readHeader.setEndTime(System.currentTimeMillis());

        /*
         读取帧数据，根据帧数据的大小创建字节数组
         */
        timeLogger.readFrame.setStartTime(System.currentTimeMillis());
        byte[] frameData = new byte[mContentLength];
        readFully(frameData);
        timeLogger.readFrame.setEndTime(System.currentTimeMillis());

        /*
         把输入字节流流转为位图
         */
        timeLogger.decodeFrame.setStartTime(System.currentTimeMillis());
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));/*方便查看图片是否解析出来*/
        long endTime = System.currentTimeMillis();
        timeLogger.decodeFrame.setEndTime(endTime);
        timeLogger.total.setEndTime(endTime);

        if (WifiCameraConstant.IS_LOG_FRAME_DATA) {
            cameraLogger.LogD("load frame " + bitmap.getWidth() + " * " + bitmap.getHeight() +
                    " : byte size = " + mContentLength + "\n" + timeLogger.getDes());
        }
        return bitmap;
    }

    private static class TimeLogger {

        private List<TimeUnit> timeUnitList;
        private TimeUnit total;
        private TimeUnit readHeader;
        private TimeUnit readFrame;
        private TimeUnit decodeFrame;

        public TimeLogger() {
            timeUnitList = new ArrayList<>();
            total = new TimeUnit("total");
            readHeader = new TimeUnit("read header");
            readFrame = new TimeUnit("read frame");
            decodeFrame = new TimeUnit("decode frame");
            timeUnitList.add(total);
            timeUnitList.add(readHeader);
            timeUnitList.add(readFrame);
            timeUnitList.add(decodeFrame);
        }

        public String getDes() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean isFirst = true;
            for (TimeUnit timeUnit : timeUnitList) {
                if (timeUnit.isEnable()) {
                    if (!isFirst) {
                        stringBuilder.append(" , ");
                    }
                    stringBuilder.append(timeUnit.getDes());
                    isFirst = false;
                }
            }
            return stringBuilder.toString();
        }
    }

    private static class TimeUnit {

        private String name;
        private long startTime = -1L;
        private long endTime = -1L;

        public TimeUnit(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getCostTime() {
            return isEnable() ? endTime - startTime : 0;
        }

        public String getDes() {
            return name + " cost " + getCostTime() + " ms";
        }

        public boolean isEnable() {
            return startTime > 0 && endTime > 0 && endTime > startTime;
        }
    }
}
