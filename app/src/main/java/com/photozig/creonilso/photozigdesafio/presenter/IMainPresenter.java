package com.photozig.creonilso.photozigdesafio.presenter;

import com.photozig.creonilso.photozigdesafio.model.Filme;

/**
 * Created by c014958 on 04/12/2017.
 */

public interface IMainPresenter extends Presenter {

    void baixarArquivos(Filme filme, int posicao);

    Filme getProximoFilme(Filme filme);
}
