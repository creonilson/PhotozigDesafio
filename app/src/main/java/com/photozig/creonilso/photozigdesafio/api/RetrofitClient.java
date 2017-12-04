package com.photozig.creonilso.photozigdesafio.api;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.photozig.creonilso.photozigdesafio.utils.ConnectivityUtil;
import java.io.File;
import java.io.IOException;
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
 * Created by c014958 on 01/12/2017.
 */

public class RetrofitClient {

    private Context mContext;

    public Retrofit getInstance(Context context) {

        mContext = context;
        File httpCacheDirectory = new File(context.getCacheDir(), CACHE_DIRETORIO);
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .cache(cache).addNetworkInterceptor(new CachingControlInterceptor())
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
            Request request = chain.request();

            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {
                if (ConnectivityUtil.isOnline(mContext)) {
                    // 1 day
                    request = request.newBuilder()
                            .header("Cache-Control", "only-if-cached")
                            .build();
                } else {
                    // 4 weeks stale
                    request = request.newBuilder()
                            .header("Cache-Control", "public, max-stale=2419200")
                            .build();
                }
            }

            Response originalResponse = chain.proceed(request);
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=600")
                    .build();
        }
    }
}
