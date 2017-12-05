package com.photozig.creonilso.photozigdesafio.asynctasks;
import android.os.AsyncTask;

import com.photozig.creonilso.photozigdesafio.Constantes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Creonilso on 04/12/2017.
 */

public class StoreFilesTask extends AsyncTask<InputStream, Integer, String> {

    private String mNomeArquivo;
    private StoreFileListener mStoreFileListener;

    public StoreFilesTask(String nome, StoreFileListener mStoreFileListener) {
        this.mNomeArquivo = nome;
        this.mStoreFileListener = mStoreFileListener;
    }

    protected String doInBackground(InputStream... inputStreams) {
        InputStream inputStream = inputStreams[0];
        final File file = new File(Constantes.LOCAL_ASSETS_DIRETORIO, mNomeArquivo);
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

        return file.getName();
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(String nome) {
        if(nome.contains(".mp3")) {
            mStoreFileListener.onDownloadFinalizado(nome);
        }

    }

    public static boolean existeArquivo(String nome){
        final File file = new File(Constantes.LOCAL_ASSETS_DIRETORIO, nome);
        return file.exists();
    }

    public boolean existeArquivo(File file){
        return file.exists();
    }

    public interface StoreFileListener {

        void onDownloadFinalizado(String path);

    }
}
