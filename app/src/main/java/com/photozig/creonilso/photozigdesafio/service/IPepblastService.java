package com.photozig.creonilso.photozigdesafio.service;

import com.photozig.creonilso.photozigdesafio.model.Pepblast;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Path;

/**
 * Created by c014958 on 01/12/2017.
 */

public interface IPepblastService {

    Observable<Pepblast> carregarConteudosPepblast();

    Observable<ResponseBody> baixarArquivo(String videoName);

}
