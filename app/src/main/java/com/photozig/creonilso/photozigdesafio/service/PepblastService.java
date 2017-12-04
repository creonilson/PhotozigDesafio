package com.photozig.creonilso.photozigdesafio.service;

import android.content.Context;

import com.photozig.creonilso.photozigdesafio.api.PepblastApi;
import com.photozig.creonilso.photozigdesafio.api.RetrofitClient;
import com.photozig.creonilso.photozigdesafio.model.Pepblast;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by c014958 on 01/12/2017.
 */

public class PepblastService implements IPepblastService {

    private  PepblastApi mPepblastApi;
    private Context mContext;

    public PepblastService(Retrofit retrofit, Context mContext) {
        this.mContext = mContext;
        this.mPepblastApi = retrofit.create(PepblastApi.class);
    }

    @Override
    public Observable<Pepblast> carregarConteudosPepblast() {
        return mPepblastApi.buscarConteudos();
    }

    @Override
    public Observable<ResponseBody> baixarVideo(String videoName) {
        return mPepblastApi.baixarVideo(videoName);
    }

}
