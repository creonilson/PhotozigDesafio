package com.photozig.creonilso.photozigdesafio.presenter;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.photozig.creonilso.photozigdesafio.api.RetrofitClient;
import com.photozig.creonilso.photozigdesafio.asynctasks.StoreFilesTask;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.photozig.creonilso.photozigdesafio.model.Pepblast;
import com.photozig.creonilso.photozigdesafio.service.PepblastService;
import com.photozig.creonilso.photozigdesafio.utils.ConnectivityUtil;
import com.photozig.creonilso.photozigdesafio.view.IMainView;

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
    public void baixarArquivos(Filme filme) {
        mDownloadArquivosList.add(filme.getVideo());
        mDownloadArquivosList.add(filme.getSom());

        PepblastService pepblastService = new PepblastService(mRetrofit);
        Observable<ResponseBody> downloadVideoObservable = pepblastService.baixarArquivo(filme.getVideo())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        Observable<ResponseBody> downloadMusicaObservable = pepblastService.baixarArquivo(filme.getSom())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        if(!StoreFilesTask.existeArquivo(filme.getVideo())) {
            Observable.concat(downloadVideoObservable, downloadMusicaObservable)
                    .subscribe(new MainPresenter.DownloadObserver());
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMainView.atualizaFragmentQuandoDownloadFinalizado();
                }
            }, DELAY_PARA_FRAGMENT_FICAR_PRONTO);
        }

        Log.i(TAG, "baixando arquivos");
    }

    @Override
    public Filme getProximoFilme(Filme filme) {
        return mFilmes.get(mFilmes.indexOf(filme) + 1);
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
    public void onDownloadFinalizado(String nomeArquivo) {
        mMainView.atualizaFragmentQuandoDownloadFinalizado();
    }

    private class DownloadObserver implements Observer<ResponseBody> {

        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onNext(ResponseBody value) {
            String nomeArquivo = mDownloadArquivosList.remove(PRIMEIRO_ITEM_DA_LISTA);
            StoreFilesTask storeFilesTask = new StoreFilesTask(nomeArquivo, MainPresenter.this);
            storeFilesTask.execute(value.byteStream());
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Erro de conexão " + e.getMessage());
        }

        @Override
        public void onComplete() {}
    }

}
