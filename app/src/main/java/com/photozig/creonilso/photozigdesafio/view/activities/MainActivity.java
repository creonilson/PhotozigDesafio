package com.photozig.creonilso.photozigdesafio.view.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.photozig.creonilso.photozigdesafio.R;
import com.photozig.creonilso.photozigdesafio.api.RetrofitClient;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.photozig.creonilso.photozigdesafio.model.Pepblast;
import com.photozig.creonilso.photozigdesafio.service.PepblastService;
import com.photozig.creonilso.photozigdesafio.view.adapter.PepblastAdapter;
import com.photozig.creonilso.photozigdesafio.view.fragments.PlayVideoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements PepblastAdapter.PepblastAdapterListener {

    @BindView(R.id.rv_pabplast)
    RecyclerView mRecyclerView;

    private PepblastAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        configurarRecycleView();

        RetrofitClient retrofitClient = new RetrofitClient();
        Retrofit retrofit = retrofitClient.getInstance(this);

        PepblastService pepblastService = new PepblastService(retrofit, this);
        pepblastService.carregarConteudosPepblast()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Pepblast>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Pepblast value) {
                Log.i("debug", "");
                mAdapter.setDataset(value.getFilmes());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.i("debug", "");
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void configurarRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PepblastAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onButtonPlayClicked(View view) {

    }

    @Override
    public void onButtonDownloadClicked(View view) {
        PlayVideoFragment fragment = new PlayVideoFragment();
        fragment.show(getSupportFragmentManager(), "play_fragment");
    }
}
