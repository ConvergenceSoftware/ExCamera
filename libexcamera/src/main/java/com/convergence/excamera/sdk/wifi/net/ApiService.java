package com.convergence.excamera.sdk.wifi.net;


import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;
import com.convergence.excamera.sdk.wifi.net.bean.NCommandResult;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

/**
 * Retrofit网络请求API
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public interface ApiService {

    /**
     * 获取图像数据流
     *
     * @return ResponseBody
     */
    @GET("?action=stream")
    @Streaming
    Observable<ResponseBody> loadStream();

    /**
     * 获取单帧图像数据
     *
     * @return ResponseBody
     */
    @GET("?action=snapshot")
    Observable<ResponseBody> loadFrame();

    /**
     * 获取所有参数配置
     *
     * @return NConfigList
     */
    @GET("input.json")
    Observable<NConfigList> loadConfig();

    /**
     * 参数操作命令
     *
     * @param queryMap 请求参数
     * @return NCommandResult
     */
    @GET("?action=command")
    Observable<NCommandResult> command(@QueryMap Map<String, String> queryMap);

    /**
     * 切换分辨率
     *
     * @param position 指loadConfig返回结果中获取的图像分辨率对应的序号
     * @return NCommandResult
     */
    @GET("?action=command&dest=0plugin=0&id=0&group=2")
    Observable<NCommandResult> updateResolution(@Query("value") int position);

    /**
     * 同步网络请求更改调焦值
     * @param focus 调焦值
     * @return NCommandResult
     */
    @GET("?action=command&id=10094858&plugin=0&dest=0&group=1")
    Call<NCommandResult> updateFocusExecute(@Query("value") int focus);
}
