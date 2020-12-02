package com.convergence.excamera.sdk.common.video;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import com.convergence.excamera.sdk.common.BitmapUtil;
import com.convergence.excamera.sdk.common.OutputUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 使用MediaMuxer进行Mp4图片合成视频
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class Mp4MediaMuxer {

    private static final String TAG = "Mp4MediaMuxer";

    private static final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    private static final int COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
    private static final long TIMEOUT_US = 10000;
    private List<MediaCodecInfo> encoderList;
    private List<MediaCodecInfo> decoderList;
    private List<MediaCodecInfo> supportEncoderList;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec mediaCodec;
    private MediaFormat outputFormat;
    private MediaMuxer mediaMuxer;
    private int videoTrackIndex;
    private boolean isMuxerStarted = false;

    public Mp4MediaMuxer() {
        encoderList = new ArrayList<>();
        decoderList = new ArrayList<>();
        supportEncoderList = new ArrayList<>();
        init();
//        logD("infoList", "encoderList");
//        printInfoList(encoderList);
//        logD("infoList", "decoderList");
//        printInfoList(decoderList);
//        logD("infoList : supportEncoderList");
//        printInfoList(supportEncoderList);
    }

    /**
     * 初始化
     */
    public void init() {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        MediaCodecInfo[] infos = mediaCodecList.getCodecInfos();
        for (MediaCodecInfo info : infos) {
            if (info.isEncoder()) {
                encoderList.add(info);
            } else {
                decoderList.add(info);
            }
        }
        for (MediaCodecInfo encoder : encoderList) {
            if (isCodecInfoSupportMime(encoder, MIME_TYPE)) {
                MediaCodecInfo.CodecCapabilities caps = encoder.getCapabilitiesForType(MIME_TYPE);
                if (isCodecCapsSupportColorFormat(caps, COLOR_FORMAT)) {
                    supportEncoderList.add(encoder);
                }
            }
        }
    }

    /**
     * 配置合成视频
     *
     * @param videoSize 视频分辨率
     * @param videoPath 视频路径
     * @param frame     帧率
     * @param listener  配置监听
     */
    public void setup(Size videoSize, String videoPath, int frame, OnSetupListener listener) {
        if (supportEncoderList.isEmpty()) {
            listener.onSetupError("device not support");
            return;
        }
        MediaFormat inputFormat = MediaFormat.createVideoFormat(MIME_TYPE, videoSize.getWidth(), videoSize.getHeight());
        inputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_FORMAT);
        //设置比特率(设置码率，通常码率越高，视频越清晰)
        inputFormat.setInteger(MediaFormat.KEY_BIT_RATE, OutputUtil.calculateBitRate(videoSize, frame));
        //设置帧率
        inputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frame);
        //关键帧间隔时间，通常情况下，你设置成多少问题都不大。
        inputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        try {
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mediaMuxer = new MediaMuxer(videoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mediaCodec.configure(inputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inputBuffers = null;
                outputBuffers = null;
            } else {
                inputBuffers = mediaCodec.getInputBuffers();
                outputBuffers = mediaCodec.getOutputBuffers();
            }
            listener.onSetupSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onSetupError(e.getMessage());
        }
    }

    /**
     * 开始解码编码
     */
    public void start() {
        mediaCodec.start();
    }

    /**
     * 将图片Bitmap进行界面
     *
     * @param bitmap 图片Bitmap
     * @param timeUs 当前图片对应的时间戳
     */
    public void encodeData(Bitmap bitmap, long timeUs) {
        int inputBufferIndex = -1;
        try {
            inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_US);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logD("feed Data : inputBufferIndex = " + inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
            } else {
                inputBuffer = inputBuffers[inputBufferIndex];
            }
            if (inputBuffer == null) {
                logD("feed Data : inputBuffer is null");
                return;
            }
            byte[] inputData = BitmapUtil.fetchNV12(bitmap);
            inputBuffer.clear();
            inputBuffer.put(inputData, 0, inputData.length);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, inputData.length, timeUs, MediaCodec.BUFFER_FLAG_KEY_FRAME);
            logD("feed Data : complete");
        }
    }

    /**
     * 解码数据合成视频
     */
    public void decodeData() {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = MediaCodec.INFO_TRY_AGAIN_LATER;
        try {
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (outputBufferIndex < 0) {
            switch (outputBufferIndex) {
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    logD("dequeueOutput Buffer timeout");
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    logD("output format changed");
                    outputFormat = mediaCodec.getOutputFormat();
                    videoTrackIndex = mediaMuxer.addTrack(outputFormat);
                    mediaMuxer.start();
                    isMuxerStarted = true;
                    logD("media muxer started");
                    break;
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    logD("output buffer changed");
                    outputBuffers = mediaCodec.getOutputBuffers();
                    break;
                default:
                    logD("output buffer index < 0 : " + outputBufferIndex);
                    break;
            }
        } else {
            logD("output buffer index : " + outputBufferIndex);
            ByteBuffer outputBuffer;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
            } else {
                outputBuffer = outputBuffers[outputBufferIndex];
            }
            if (outputBuffer == null) {
                logD("outputBuffer is null");
            }

            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                bufferInfo.size = 0;
            }
            logD("bufferInfo size = " + bufferInfo.size);
            if (bufferInfo.size != 0 && isMuxerStarted) {
                outputBuffer.position(bufferInfo.offset);
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                try {
                    mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                logD("output complete");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mediaMuxer != null) {
            try {
                mediaMuxer.stop();
                mediaMuxer.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaMuxer = null;
            }
        }
        if (mediaCodec != null) {
            try {
                mediaCodec.stop();
                mediaCodec.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaCodec = null;
            }
        }
    }

    /**
     * 判断MediaCodecInfo是否支持Mime
     */
    private boolean isCodecInfoSupportMime(MediaCodecInfo info, String mime) {
        String[] types = info.getSupportedTypes();
        for (String type : types) {
            if (type.equals(mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断CodecCapabilities是否支持ColorFormat
     */
    private boolean isCodecCapsSupportColorFormat(MediaCodecInfo.CodecCapabilities caps, int format) {
        int[] colorFormats = caps.colorFormats;
        for (int colorFormat : colorFormats) {
            if (colorFormat == format) {
                return true;
            }
        }
        return false;
    }

    /**
     * 打印信息列表
     */
    private void printInfoList(List<MediaCodecInfo> infoList) {
        for (MediaCodecInfo info : infoList) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(info.toString()).append("\nName : ").append(info.getName())
                    .append("\nSupportTypes : ").append(Arrays.toString(info.getSupportedTypes()));
            if (isCodecInfoSupportMime(info, MIME_TYPE)) {
                MediaCodecInfo.CodecCapabilities caps = info.getCapabilitiesForType(MIME_TYPE);
                stringBuilder.append("\nColorFormats : ").append(Arrays.toString(caps.colorFormats));
            }
            logD("infoList :\n" + stringBuilder.toString());
        }
    }

    private void logD(String msg) {
        Log.d(TAG, msg);
    }

    public interface OnSetupListener {

        /**
         * 初始化配置成功
         */
        void onSetupSuccess();

        /**
         * 初始化配置出错
         *
         * @param error 错误信息
         */
        void onSetupError(String error);
    }
}
