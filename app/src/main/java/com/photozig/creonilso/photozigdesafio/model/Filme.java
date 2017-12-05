package com.photozig.creonilso.photozigdesafio.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;
import org.parceler.Parcels;
import org.parceler.converter.ArrayListParcelConverter;

import java.util.List;

/**
 * Created by c014958 on 01/12/2017.
 */
@Parcel(Parcel.Serialization.BEAN)
public class Filme {

    @SerializedName("name")
    private String nome;
    @SerializedName("bg")
    private String video;
    @SerializedName("im")
    private String imagem;
    @SerializedName("sg")
    private String som;
    @SerializedName("txts")
    @ParcelPropertyConverter(ItemListParcelConverter.class)
    private List<TextoFilme> listaDeTextos;

    public class ItemListParcelConverter extends ArrayListParcelConverter<TextoFilme> {

        @Override
        public void itemToParcel(TextoFilme input, android.os.Parcel parcel) {
            parcel.writeParcelable(Parcels.wrap(input), 0);
        }

        @Override
        public TextoFilme itemFromParcel(android.os.Parcel parcel) {
            return Parcels.unwrap(parcel.readParcelable(TextoFilme.class.getClassLoader()));
        }
    }

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

    public String getSom() {
        return som;
    }

    public void setSom(String som) {
        this.som = som;
    }

}
