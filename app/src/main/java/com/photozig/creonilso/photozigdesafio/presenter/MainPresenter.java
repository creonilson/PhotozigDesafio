package com.photozig.creonilso.photozigdesafio.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.photozig.creonilso.photozigdesafio.api.RetrofitClient;
import com.photozig.creonilso.photozigdesafio.asynctasks.StoreFilesTask;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.photozig.creonilso.photozigdesafio.model.Pepblast;
import com.photozig.creonilso.photozigdesafio.service.PepblastService;
import com.photozig.creonilso.photozigdesafio.view.IMainView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static com.photozig.creonilso.photozigdesafio.Constantes.PRIMEIRO_ITEM_DA_LISTA;

/**
 * Created by Creonilso on 04/12/2017.
 */

public class MainPresenter implements IMainPresenter, Observer<Pepblast>, StoreFilesTask.StoreFileListener {


    public static final int DELAY_PARA_FRAGMENT_FICAR_PRONTO = 2000;
    private List<String> mDownloadArquivosList;
    private IMainView mMainView;
    private List<Filme> mFilmes;
    private Retrofit mRetrofit;
    private static final String TAG = MainPresenter.class.getName();

    public MainPresenter(IMainView mainView) {
        this.mMainView = mainView;
    }

    @Override
    public void inicializa() {
        mDownloadArquivosList = new ArrayList<>();
        RetrofitClient retrofitClient = new RetrofitClient();
        mRetrofit = retrofitClient.getInstance(mMainView.getContext());

        PepblastService pepblastService = new PepblastService(mRetrofit);
        pepblastService.carregarConteudosPepblast()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this);
    }

    @Override
    public void baixarArquivos(Filme filme, final int posicao) {
       // mDownloadArquivosList.add(filme.getVideo());
        //mDownloadArquivosList.add(filme.getSom());

        PepblastService pepblastService = new PepblastService(mRetrofit);
        Observable<ResponseBody> downloadVideoObservable = pepblastService.baixarArquivo(filme.getVideo())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        Observable<ResponseBody> downloadMusicaObservable = pepblastService.baixarArquivo(filme.getSom())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        if(!StoreFilesTask.existeArquivo(filme.getVideo())) {
            Observable.concat(downloadVideoObservable, downloadMusicaObservable)
                    .subscribe(new MainPresenter.DownloadObserver(filme, posicao));
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMainView.onDownloadArquivoFinalizado(posicao);
                }
            }, DELAY_PARA_FRAGMENT_FICAR_PRONTO);
        }

        Log.i(TAG, "baixando arquivos");
    }

    @Override
    public Filme getProximoFilme(Filme filme) {
        int indice = mFilmes.indexOf(filme);
        if((indice + 1) >= mFilmes.size()){
            return mFilmes.get(indice);
        } else {
            return mFilmes.get(indice + 1);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {}

    @Override
    public void onNext(Pepblast value) {
        mMainView.atualizarAdapter(value.getFilmes());
        mFilmes = value.getFilmes();
        mMainView.trocaVisibilidadeTextviewSemInternet(!mFilmes.isEmpty());
    }

    @Override
    public void onError(Throwable e) {
        Log.i(TAG, "Erro de conexão: " + e.getMessage());
        boolean temDados = mFilmes != null && !mFilmes.isEmpty();
        mMainView.trocaVisibilidadeTextviewSemInternet(temDados);
    }

    @Override
    public void onComplete() {}

    @Override
    public void onDownloadFinalizado(int posicao) {
        mMainView.onDownloadArquivoFinalizado(posicao);
    }

    private class DownloadObserver implements Observer<ResponseBody> {

        public static final int POSICAO_VIDEO = 0;
        public static final int POSICAO_SOM = 1;
        private Filme filme;
        private int posicao;
        private List<InputStream> stream;

        public DownloadObserver(Filme filme,int posicao) {
            this.filme = filme;
            this.posicao = posicao;
            this.stream = new ArrayList<>();
        }

        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onNext(ResponseBody value) {
           stream.add(value.byteStream());
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Erro de conexão " + e.getMessage());
        }

        @Override
        public void onComplete() {
            StoreFilesTask storeFilesTask = new StoreFilesTask(posicao, filme, MainPresenter.this);
            storeFilesTask.execute(stream.get(POSICAO_VIDEO), stream.get(POSICAO_SOM));

        }
    }

}
