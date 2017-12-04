package com.photozig.creonilso.photozigdesafio.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by c014958 on 01/12/2017.
 */

public class TextoFilme {

    @SerializedName("txt")
    private String titulo;
    @SerializedName("time")
    private String tempo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
}
