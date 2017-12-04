package com.photozig.creonilso.photozigdesafio.view.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.photozig.creonilso.photozigdesafio.R;
import com.photozig.creonilso.photozigdesafio.api.RetrofitClient;
import com.photozig.creonilso.photozigdesafio.service.PepblastService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Creonilso on 02/12/2017.
 */

public class PlayVideoFragment extends DialogFragment
{
    @BindView(R.id.video_view)
    VideoView mVideoView;
    @BindView(R.id.txv_titulo)
    TextView mTxvTituloFilme;
    @BindView(R.id.txv_tempo)
    TextView mTxvTempoFilme;
    @BindView(R.id.pb_video_download)
    ProgressBar mProgressDownloadVideo;

    public PlayVideoFragment()
    {
        // Required empty public constructor
    }

    public static PlayVideoFragment newInstance()
    {
        return new PlayVideoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.play_video_fragment, container, false);
        if (v != null) {
            ButterKnife.bind(this, v);
            RetrofitClient retrofitClient = new RetrofitClient();
            Retrofit retrofit = retrofitClient.getInstance(v.getContext());

            PepblastService pepblastService = new PepblastService(retrofit, v.getContext());
            pepblastService.baixarVideo("440-BlueLines.mp4")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(ResponseBody value) {

                            StoreFilesTask storeFilesTask = new StoreFilesTask();
                            storeFilesTask.execute(value.byteStream());
                            mVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.setLooping(true);
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onComplete() {}
                    });
        }
        return v;
    }

    private class StoreFilesTask extends AsyncTask<InputStream, Integer, String> {

        protected String doInBackground(InputStream... inputStreams) {
            InputStream inputStream = inputStreams[0];
            final File file = new File(Environment.getExternalStorageDirectory() + File.separator + "440-BlueLines.mp4" );
            try {
                OutputStream output = new FileOutputStream(file);

                try {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                } finally {
                    output.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // publishProgress((int) ((i / (float) count) * 100));

            return file.getAbsolutePath();
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(String path) {
            mProgressDownloadVideo.setVisibility(View.GONE);
            MediaController mediaController = new MediaController(getContext());
            mediaController.setVisibility(View.GONE);
            mediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(Uri.parse(path));
            mVideoView.requestFocus();
            mVideoView.start();

        }
    }

}
