package com.convergence.excamera.sdk.planet.net;

import com.convergence.excamera.sdk.planet.PlanetConstant;
import com.convergence.excamera.sdk.planet.net.bean.NControlBean;
import com.convergence.excamera.sdk.planet.net.bean.NControlResult;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Retrofit网络请求API
 *
 * @Author LiLei
 * @CreateDate 2021-05-24
 * @Organization Convergence Ltd.
 */
public interface ApiService {

    /**
     * 云台运动控制
     *
     * @return Gson
     */


    @GET(PlanetConstant.DEFAULT_PLANET_CONTROL_URL)
    Observable<NControlResult> control(@QueryMap Map<String, String> queryMap);

    @Headers({
            "CONNECT_TIMEOUT:150000", "READ_TIMEOUT:150000", "WRITE_TIMEOUT:150000"})
    @GET(PlanetConstant.DEFAULT_PLANET_CONTROL_URL)
    Observable<NControlResult> reset(@QueryMap Map<String, String> queryMap);

    @Headers({
            "CONNECT_TIMEOUT:1000", "READ_TIMEOUT:1000", "WRITE_TIMEOUT:1000"})
    @GET(PlanetConstant.DEFAULT_PLANET_CONTROL_URL)
    Observable<NControlResult> controlStop(@QueryMap Map<String, String> queryMap);



    @GET(PlanetConstant.DEFAULT_PLANET_CONTROL_URL)
    Call<NControlResult> controlExecute(@QueryMap Map<String, String> queryMap);

}
