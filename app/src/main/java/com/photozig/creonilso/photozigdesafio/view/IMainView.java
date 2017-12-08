package com.photozig.creonilso.photozigdesafio.view;

import com.photozig.creonilso.photozigdesafio.model.Filme;

import java.util.List;

/**
 * Created by c014958 on 04/12/2017.
 */

public interface IMainView extends IView {

    void atualizarAdapter(List<Filme> list);

    void onDownloadArquivoFinalizado(int posicao);

    void trocaVisibilidadeTextviewSemInternet(boolean temDados);
}
