package com.convergence.excamera.sdk.wifi.net;

import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfig;
import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;
import com.convergence.excamera.sdk.wifi.net.bean.NCommandResult;
import com.convergence.excamera.sdk.wifi.net.callback.NetCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * WiFi连接网络请求封装
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraNetWork {

    private static final int DEFAULT_TIMEOUT = 30;

    private CameraLogger cameraLogger = WifiCameraConstant.GetLogger();
    private String baseUrl = WifiCameraConstant.DEFAULT_BASE_URL;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HttpLoggingInterceptor loggingInterceptor;

    private WifiCameraNetWork() {
        init();
    }

    public static WifiCameraNetWork getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final WifiCameraNetWork INSTANCE = new WifiCameraNetWork();
    }

    /**************************************以上是单例模块*******************************************/

    private void init() {
        loggingInterceptor = new HttpLoggingInterceptor(s -> cameraLogger.LogI(s));
    }

    private void initRetrofit() {
        if (okHttpClient == null) {
            okHttpClient = getDefaultOkHttpClient();
        }
        initRetrofit(okHttpClient);
    }

    private void initRetrofit(OkHttpClient okHttpClient) {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient getDefaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    private ApiService getApiService() {
        if (retrofit == null) {
            initRetrofit();
        }
        return retrofit.create(ApiService.class);
    }

    private void subscribe(Observable observable, NetCallback callback) {
        if (callback != null) {
            callback.onStart();
        }
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        if (callback != null) {
                            callback.onDone(o);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 更新网络请求IP地址
     */
    public void updateBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        initRetrofit();
    }

    /**
     * 获取图像数据流
     */
    public void loadStream(NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.HEADERS);
        Observable<ResponseBody> observable = getApiService().loadStream();
        subscribe(observable, netCallback);
    }

    /**
     * 加载单帧图像
     */
    public void loadFrame(NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.HEADERS);
        Observable<ResponseBody> observable = getApiService().loadFrame();
        subscribe(observable, netCallback);
    }

    /**
     * 获取配置信息
     */
    public void loadConfig(NetCallback netCallback, boolean isLog) {
        loggingInterceptor.level(isLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
        Observable<NConfigList> observable = getApiService().loadConfig();
        subscribe(observable, netCallback);
    }

    /**
     * 发送调节参数命令
     *
     * @param wifiConfig 参数对应的配置封装类
     */
    public void command(WifiConfig wifiConfig, NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        Observable<NCommandResult> observable = getApiService().command(createCommandMap(wifiConfig));
        subscribe(observable, netCallback);
    }

    /**
     * 更新分辨率
     *
     * @param position 该分辨率在配置信息中分辨率列表的位置
     */
    public void updateResolution(int position, NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        Observable<NCommandResult> observable = getApiService().updateResolution(position);
        subscribe(observable, netCallback);
    }

    /**
     * 同步网络请求更新对焦
     *
     * @param focus 参数
     */
    public boolean updateFocusExecute(int focus) {
        Call<NCommandResult> call = getApiService().updateFocusExecute(focus);
        try {
            Response<NCommandResult> response = call.execute();
            NCommandResult result = response.body();
            if (result != null) {
                return result.isSuccess();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    /**
//     * 同步网络请求更新对焦
//     *
//     * @param focus 参数
//     */
//    public boolean updateFocusExecute(int focus) {
//        String url = WifiCameraConstant.DEFAULT_BASE_URL +
//                "?action=command&id=10094858&plugin=0&dest=0&group=1&value=" + focus;
//        Call call = okHttpClient.newCall(new Request.Builder()
//                .url(url)
//                .build());
//        try {
//            Response responseBody = call.execute();
//            if (responseBody.isSuccessful()) {
//                ResponseBody body = responseBody.body();
//                if (body != null) {
//                    Gson gson = new Gson();
//                    NCommandResult result = gson.fromJson(body.string(), NCommandResult.class);
//                    return result.isSuccess();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 根据参数对应的配置封装类创建请求表
     */
    private Map<String, String> createCommandMap(WifiConfig wifiConfig) {
        Map<String, String> map = new HashMap<>();
//        map.put("action", "command");
        map.put("id", wifiConfig.getId());
        map.put("plugin", "0");
        map.put("dest", wifiConfig.getDest() + "");
        map.put("group", wifiConfig.getGroup() + "");
        map.put("value", wifiConfig.getUpdate() + "");
        return map;
    }
}
