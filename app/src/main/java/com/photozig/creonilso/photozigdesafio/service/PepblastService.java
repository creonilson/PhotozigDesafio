package com.photozig.creonilso.photozigdesafio.service;

import com.photozig.creonilso.photozigdesafio.api.PepblastApi;
import com.photozig.creonilso.photozigdesafio.model.Pepblast;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by c014958 on 01/12/2017.
 */

public class PepblastService implements IPepblastService {

    private  PepblastApi mPepblastApi;

    public PepblastService(Retrofit retrofit) {
        this.mPepblastApi = retrofit.create(PepblastApi.class);
    }

    @Override
    public Observable<Pepblast> carregarConteudosPepblast() {
        return mPepblastApi.buscarConteudos();
    }

    @Override
    public Observable<ResponseBody> baixarArquivo(String arquivoName) {
        return mPepblastApi.baixarArquivo(arquivoName);
    }

}
