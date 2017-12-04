package com.photozig.creonilso.photozigdesafio.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by c014958 on 01/12/2017.
 */

public class Filme {

    @SerializedName("name")
    private String nome;
    @SerializedName("bg")
    private String video;
    @SerializedName("im")
    private String imagem;
    @SerializedName("txts")
    private List<TextoFilme> listaDeTextos;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public List<TextoFilme> getListaDeTextos() {
        return listaDeTextos;
    }

    public void setListaDeTextos(List<TextoFilme> listaDeTextos) {
        this.listaDeTextos = listaDeTextos;
    }
}
