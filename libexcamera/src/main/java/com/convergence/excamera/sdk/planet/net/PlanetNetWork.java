package com.convergence.excamera.sdk.planet.net;


import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.planet.PlanetConstant;
import com.convergence.excamera.sdk.planet.net.bean.NControlBean;
import com.convergence.excamera.sdk.planet.net.bean.NControlResult;
import com.convergence.excamera.sdk.planet.net.callback.NetCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Planet连接网络请求封装
 *
 * @Author LiLei
 * @CreateDate 2021-05-24
 * @Organization Convergence Ltd.
 */
public class PlanetNetWork {
    private static final int DEFAULT_TIMEOUT = 60*10;

    private CameraLogger cameraLogger = PlanetConstant.GetLogger();
    private String baseUrl = PlanetConstant.DEFAULT_PLANET_CONTROL_URL;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HttpLoggingInterceptor loggingInterceptor;

    private PlanetNetWork() {
        init();
    }

    public static PlanetNetWork getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PlanetNetWork INSTANCE = new PlanetNetWork();
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
                .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器。
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new TimeoutInterceptor())
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
     * 发送云台复位命令
     *
     */
    public void reset(NControlBean controlBean, NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        Observable<NControlResult> observable = getApiService().reset(createControlMap(controlBean));
        subscribe(observable, netCallback);
    }

    /**
     * 发送云台运动控制命令
     *
     */
    public void control(NControlBean controlBean, NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        Observable<NControlResult> observable = getApiService().control(createControlMap(controlBean));
        subscribe(observable, netCallback);
    }
    /**
     * 发送云台停止控制命令
     *
     */
    public void controlStop(NControlBean controlBean, NetCallback netCallback) {
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        Observable<NControlResult> observable = getApiService().controlStop(createControlMap(controlBean));
        subscribe(observable, netCallback);
    }

    /**
     * 发送云台运动同步控制命令
     *
     */
    public NControlResult controlExecute(NControlBean controlBean) {
        Call<NControlResult> call = getApiService().controlExecute(createControlMap(controlBean));
        try {
            Response<NControlResult> response = call.execute();
            NControlResult result = response.body();
            if (result != null) {
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据参数对应的配置封装类创建请求体
     */
    private RequestBody createControlRequestBody(NControlBean controlBean) {

        String jsonString=new Gson().toJson(controlBean);
        cameraLogger.LogE(jsonString);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),jsonString);

        return requestBody;
    }

    /**
     * 根据参数对应的配置封装类创建请求表
     */
    private Map<String, String> createControlMap(NControlBean controlBean) {
        Map<String, String> map = new HashMap<>();
        map.put("id", controlBean.getId()+ "");
        map.put("controlType", controlBean.getControlType() + "");
        map.put("time", controlBean.getTime() + "");
        map.put("mode", controlBean.getMode() + "");
        map.put("speed", controlBean.getSpeed() + "");
        map.put("subDivision", controlBean.getSubDivision() + "");
        map.put("returnTrip",controlBean.getReturnTrip()+"");
        map.put("returnTripTime", controlBean.getReturnTripTime()+"");
        return map;
    }

}
