package com.convergence.excamera.sdk.planet.net;

import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 自定义的超时拦截器
 * @Author LiLei
 * @CreateDate 2021-08-18
 * @Organization Convergence Ltd.
 */
public class TimeoutInterceptor implements Interceptor {


    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";

    @Override
    public Response intercept(Chain chain) throws IOException {

        int connectTimeout = chain.connectTimeoutMillis();
        int readTimeout = chain.readTimeoutMillis();
        int writeTimeout = chain.writeTimeoutMillis();
        Request request = chain.request();
        String connectNew = request.header(CONNECT_TIMEOUT);
        String readNew = request.header(READ_TIMEOUT);
        String writeNew = request.header(WRITE_TIMEOUT);

        if (!TextUtils.isEmpty(connectNew)) {
            connectTimeout = Integer.parseInt(connectNew);
        }
        if (!TextUtils.isEmpty(readNew)) {

            readTimeout = Integer.parseInt(readNew);
        }
        if (!TextUtils.isEmpty(writeNew)) {

            writeTimeout = Integer.parseInt(writeNew);
        }
        return chain
                .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request);
    }
}