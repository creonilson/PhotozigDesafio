package com.photozig.creonilso.photozigdesafio.api;

import com.photozig.creonilso.photozigdesafio.model.Pepblast;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by c014958 on 01/12/2017.
 */

public interface PepblastApi {

    @GET("/pz_challenge/assets.json")
    Observable<Pepblast> buscarConteudos();

    @Streaming
    @GET("/pz_challenge/assets/{arquivo_name}")
    Observable<ResponseBody> baixarArquivo(@Path("arquivo_name")String arquivoNome);

}
