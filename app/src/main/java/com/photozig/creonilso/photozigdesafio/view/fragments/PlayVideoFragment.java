package com.photozig.creonilso.photozigdesafio.view.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.photozig.creonilso.photozigdesafio.Constantes;
import com.photozig.creonilso.photozigdesafio.R;
import com.photozig.creonilso.photozigdesafio.api.RetrofitClient;
import com.photozig.creonilso.photozigdesafio.asynctasks.StoreFilesTask;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.photozig.creonilso.photozigdesafio.model.TextoFilme;
import com.photozig.creonilso.photozigdesafio.service.PepblastService;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static com.photozig.creonilso.photozigdesafio.Constantes.PRIMEIRO_ITEM_DA_LISTA;

/**
 * Created by Creonilso on 02/12/2017.
 */

public class PlayVideoFragment extends DialogFragment implements MediaPlayer.OnCompletionListener {

    @BindView(R.id.video_view)
    VideoView mVideoView;
    @BindView(R.id.txv_titulo)
    TextView mTxvTituloFilme;
    @BindView(R.id.txv_tempo)
    TextView mTxvTempoFilme;
    @BindView(R.id.pb_video_download)
    ProgressBar mProgressDownloadVideo;
    @BindView(R.id.btn_proximo)
    Button mBtnProximo;

    private PlayVideoFragmentListener mPlayVideoFragmentListener;
    private MediaPlayer mMediaPlayer;
    private static final String FILME_KEY = "filme_key";
    private Filme mFilme;
    private MediaController mMediaController;

    public static PlayVideoFragment newInstance(Filme filme)
    {
        PlayVideoFragment playVideoFragment = new PlayVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FILME_KEY, Parcels.wrap(filme));
        playVideoFragment.setArguments(bundle);
        return playVideoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaController = new MediaController(getContext());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.play_video_fragment, container, false);
        if (v != null) {
            ButterKnife.bind(this, v);
            mFilme = Parcels.unwrap(getArguments().getParcelable(FILME_KEY));
            mostrarTextosDoFilme();
            mMediaController.setAnchorView(mVideoView);

            mVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
        }
        return v;
    }

    private void mostrarTextosDoFilme() {
        if (mFilme != null) {
            if(mFilme.getListaDeTextos() != null && !mFilme.getListaDeTextos().isEmpty()) {
                TextoFilme textoFilme = mFilme.getListaDeTextos().get(PRIMEIRO_ITEM_DA_LISTA);
                mTxvTempoFilme.setText(String.format("%s%s", textoFilme.getTempo(), getString(R.string.minutos)));
                mTxvTituloFilme.setText(textoFilme.getTitulo());
            }else {
                mTxvTempoFilme.setVisibility(View.GONE);
                mTxvTituloFilme.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_fechar)
    public void onBtnFecharClicked(View view){
        pararAudio();
        pararVideo();
        getDialog().dismiss();
    }

    @OnClick(R.id.btn_proximo)
    public void onBtnProximoClicked(View view){
        mPlayVideoFragmentListener.onBtnProximoClicked(view, mFilme);
    }

    public void tocarFilme() {
        mProgressDownloadVideo.setVisibility(View.GONE);
        try {
            if(mMediaPlayer != null){
                mMediaPlayer.reset();
            }
            mMediaPlayer.setDataSource(getActivity(), Uri.parse(Constantes.LOCAL_ASSETS_DIRETORIO
                    + File.separator + mFilme.getSom()));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoURI(Uri.parse(Constantes.LOCAL_ASSETS_DIRETORIO + File.separator + mFilme.getVideo()));
        mVideoView.requestFocus();
        mVideoView.start();
        mBtnProximo.setEnabled(true);
    }

    public void mostrarProximoFilme(Filme filme){
        mFilme = filme;
        mBtnProximo.setEnabled(false);
        mostrarTextosDoFilme();
        mProgressDownloadVideo.setVisibility(View.VISIBLE);
        pararAudio();
        pararVideo();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mVideoView.pause();
    }

    @Override
    public void onPause() {
        super.onPause();
        pararAudio();

    }

    private void pararAudio() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
        }
    }

    private void pararVideo() {
        if(mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setmPlayVideoFragmentListener(PlayVideoFragmentListener mPlayVideoFragmentListener) {
        this.mPlayVideoFragmentListener = mPlayVideoFragmentListener;
    }

    public interface PlayVideoFragmentListener {

        void onBtnProximoClicked(View view, Filme filme);

    }

}
