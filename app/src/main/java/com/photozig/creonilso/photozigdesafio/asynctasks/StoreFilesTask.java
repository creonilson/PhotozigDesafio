package com.photozig.creonilso.photozigdesafio.asynctasks;
import android.os.AsyncTask;

import com.photozig.creonilso.photozigdesafio.Constantes;
import com.photozig.creonilso.photozigdesafio.model.Filme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Creonilso on 04/12/2017.
 */

public class StoreFilesTask extends AsyncTask<InputStream, Integer, Integer> {

    public static final int POSICAO_VIDEO = 0;
    public static final int POSICAO_SOM = 1;
    private Filme mfilme;
    private StoreFileListener mStoreFileListener;
    private int posicao;

    public StoreFilesTask(int posicao, Filme filme, StoreFileListener mStoreFileListener) {
        this.mfilme = filme;
        this.mStoreFileListener = mStoreFileListener;
        this.posicao = posicao;
    }

    protected Integer doInBackground(InputStream... inputStreams) {
        InputStream inputStreamVideo = inputStreams[POSICAO_VIDEO];
        final File fileVideo = new File(Constantes.LOCAL_ASSETS_DIRETORIO, mfilme.getVideo());
        salvarArquivo(inputStreamVideo, fileVideo);

        InputStream inputStreamSom = inputStreams[POSICAO_SOM];
        final File fileMusica = new File(Constantes.LOCAL_ASSETS_DIRETORIO, mfilme.getSom());
        salvarArquivo(inputStreamSom, fileMusica);

        return posicao;
    }

    private void salvarArquivo(InputStream inputStream, File file) {
        if(!file.exists()) {
            try {

                try (OutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4 * 1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(Integer posicao) {
        mStoreFileListener.onDownloadFinalizado(posicao);

    }

    public static boolean existeArquivo(String nome){
        final File file = new File(Constantes.LOCAL_ASSETS_DIRETORIO, nome);
        return file.exists();
    }

    public boolean existeArquivo(File file){
        return file.exists();
    }

    public interface StoreFileListener {

        void onDownloadFinalizado(int posicao);

    }
}
