package com.photozig.creonilso.photozigdesafio.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by c014958 on 01/12/2017.
 */

public class Pepblast {

    //@SerializedName("assetsLocation")
    //private String assetsLocation;
    @SerializedName("objects")
    private List<Filme> filmes;
/*

    public String getAssetsLocation() {
        return assetsLocation;
    }

    public void setAssetsLocation(String assetsLocation) {
        this.assetsLocation = assetsLocation;
    }
*/

    public List<Filme> getFilmes() {
        return filmes;
    }

    public void setFilmes(List<Filme> filmes) {
        this.filmes = filmes;
    }
}
