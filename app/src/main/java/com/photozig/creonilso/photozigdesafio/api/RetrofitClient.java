package com.photozig.creonilso.photozigdesafio.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.photozig.creonilso.photozigdesafio.utils.ConnectivityUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.photozig.creonilso.photozigdesafio.Constantes.CACHE_DIRETORIO;
import static com.photozig.creonilso.photozigdesafio.Constantes.PEPBLAST_BASE_URL;

/**
 * Created by Creonilso on 01/12/2017.
 */

public class RetrofitClient {

    private static final int CACHE_SIZE = 200 * 1024 * 1024;
    private Context mContext;

    public Retrofit getInstance(Context context) {

        mContext = context;
        File httpCacheDirectory = new File(context.getCacheDir(), CACHE_DIRETORIO);
        Cache cache = new Cache(httpCacheDirectory, CACHE_SIZE);
        CachingControlInterceptor cachingControlInterceptor = new CachingControlInterceptor();
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .cache(cache).connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(cachingControlInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return  new Retrofit.Builder()
                .baseUrl(PEPBLAST_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    private class CachingControlInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            try {
                return chain.proceed(chain.request());
            } catch (Exception e) {
                Request offlineRequest = chain.request().newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24)
                        .build();
                return chain.proceed(offlineRequest);
            }
        }
    }
}
