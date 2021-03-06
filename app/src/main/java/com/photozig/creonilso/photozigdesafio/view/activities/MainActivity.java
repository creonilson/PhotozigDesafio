package com.photozig.creonilso.photozigdesafio.view.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.photozig.creonilso.photozigdesafio.R;
import com.photozig.creonilso.photozigdesafio.asynctasks.StoreFilesTask;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.photozig.creonilso.photozigdesafio.presenter.IMainPresenter;
import com.photozig.creonilso.photozigdesafio.presenter.MainPresenter;
import com.photozig.creonilso.photozigdesafio.utils.ConnectivityUtil;
import com.photozig.creonilso.photozigdesafio.view.IMainView;
import com.photozig.creonilso.photozigdesafio.view.adapter.PepblastAdapter;
import com.photozig.creonilso.photozigdesafio.view.fragments.PlayVideoFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PepblastAdapter.PepblastAdapterListener,
        IMainView, PlayVideoFragment.PlayVideoFragmentListener {

    private static final int REQUEST_PERMISSAO_PARA_ESCREVER_NO_SDCARD = 10;
    public static final int POSICAO_INEXISTENTE = -1;
    @BindView(R.id.rv_pabplast)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_sem_internet)
    TextView mTvSemInternet;

    public static final String PLAY_FRAGMENT = "play_fragment";
    private PepblastAdapter mAdapter;
    private IMainPresenter mainPresenter;
    private PlayVideoFragment mFragmentPlayVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        configurarRecycleView();
        mainPresenter = new MainPresenter(this);
        mainPresenter.inicializa();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSAO_PARA_ESCREVER_NO_SDCARD: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //// TODO: 07/12/2017 chamar tela de videos e executar
                } else {
                }
                return;
            }
        }
    }

    private void configurarRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PepblastAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onButtonPlayClicked(View view, Filme filme) {
        if(temInternetEArquivoNaoExiste(filme.getVideo())){
            if(checkSelfiePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mostrarTelaPlayVideoFragment(filme);
                mainPresenter.baixarArquivos(filme, POSICAO_INEXISTENTE);
            }else {
                solicitaPermissao(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSAO_PARA_ESCREVER_NO_SDCARD);
            }
        }
    }

    private void mostrarTelaPlayVideoFragment(Filme filme) {
        mFragmentPlayVideo = PlayVideoFragment.newInstance(filme);
        mFragmentPlayVideo.setmPlayVideoFragmentListener(this);
        mFragmentPlayVideo.show(getSupportFragmentManager(), PLAY_FRAGMENT);
    }

    @Override
    public void onButtonDownloadClicked(View view, Filme filme, int posicao) {
       if(temInternetEArquivoNaoExiste(filme.getVideo())){
            if(checkSelfiePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mainPresenter.baixarArquivos(filme, posicao);
            }else {
                solicitaPermissao(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSAO_PARA_ESCREVER_NO_SDCARD);
            }
        }
    }

    @Override
    public void atualizarAdapter(List<Filme> list) {
        mAdapter.setDataset(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDownloadArquivoFinalizado(int posicao) {
        if(posicao != POSICAO_INEXISTENTE) {
            mAdapter.removeProgressDownload(posicao);
        } else {
            mFragmentPlayVideo.tocarFilme();
        }
    }

    @Override
    public void trocaVisibilidadeTextviewSemInternet(boolean temDados) {
        mTvSemInternet.setVisibility(temDados ? View.GONE : View.VISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBtnProximoClicked(View view, Filme filme) {
        Filme proximoFilmo = mainPresenter.getProximoFilme(filme);
        mFragmentPlayVideo.mostrarProximoFilme(proximoFilmo);
        mainPresenter.baixarArquivos(proximoFilmo, POSICAO_INEXISTENTE);
    }

    private boolean checkSelfiePermission(String permissao){
        int permissionCheck = ContextCompat.checkSelfPermission(this, permissao);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitaPermissao(String permissao, int requestPermissao){
        ActivityCompat.requestPermissions(this, new String[]{permissao},
                requestPermissao);
    }

    private boolean temInternetEArquivoNaoExiste(String nome){
        if (!StoreFilesTask.existeArquivo(nome) && !ConnectivityUtil.isOnline(this)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.titulo_sem_conexao_com_internet)
                    .setMessage(R.string.msg_sem_conexao_internet)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return false;
        }

        return true;
    }
}
