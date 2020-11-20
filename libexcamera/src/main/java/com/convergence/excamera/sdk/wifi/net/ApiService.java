package com.convergence.excamera.sdk.wifi.net;


import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;
import com.convergence.excamera.sdk.wifi.net.bean.NCommandResult;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
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

    //获取图像数据流
    @GET("?action=stream")
    @Streaming
    Observable<ResponseBody> loadStream();

    //获取单帧图像数据
    @GET("?action=snapshot")
    Observable<ResponseBody> loadFrame();

    //获取所有参数配置
    @GET("input.json")
    Observable<NConfigList> loadConfig();

    //参数操作命令
    @GET("?action=command")
    Observable<NCommandResult> command(@QueryMap Map<String, String> queryMap);

    //切换分辨率
    //position指loadConfig返回结果中获取的图像分辨率对应的序号
    @GET("?action=command&dest=0plugin=0&id=0&group=2")
    Observable<NCommandResult> updateResolution(@Query("value") int position);
}
