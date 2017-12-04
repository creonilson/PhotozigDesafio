package com.photozig.creonilso.photozigdesafio.view.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photozig.creonilso.photozigdesafio.R;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.photozig.creonilso.photozigdesafio.view.fragments.PlayVideoFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.photozig.creonilso.photozigdesafio.Constantes.PEPBLAST_ASSETS_PATH;
import static com.photozig.creonilso.photozigdesafio.Constantes.PEPBLAST_BASE_URL;

/**
 * Created by Creonilso on 01/12/2017.
 */

public class PepblastAdapter extends RecyclerView.Adapter<PepblastAdapter.ViewHolder> {

    private List<Filme> mDataset;
    private PepblastAdapterListener mPepblastAdapterListener;

    public PepblastAdapter(PepblastAdapterListener pepblastAdapterListener) {
        this.mPepblastAdapterListener = pepblastAdapterListener;
        mDataset = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_nome)
        TextView mTextView;
        @BindView(R.id.img_filme)
        ImageView mImvFilme;
        @BindView(R.id.progress_imagem_fundo)
        ProgressBar mProgressImagemFundo;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.btn_baixar)
        public void onButtonDownloadClicked(View view){
            //baixar imagem
            PepblastAdapter.this.getPepblastAdapterListener().onButtonDownloadClicked(view);
        }

        @OnClick(R.id.btn_visualizar)
        public void onButtonPlayClicked(View view){
            //ir para a pagina de exibir videos
            PepblastAdapter.this.getPepblastAdapterListener().onButtonPlayClicked(view);
        }

    }

    public void setDataset(List<Filme> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public PepblastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pepblast_item, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position).getNome());
        String urlImagem = PEPBLAST_BASE_URL + PEPBLAST_ASSETS_PATH + mDataset.get(position).getImagem();
        Picasso.with(holder.mImvFilme.getContext())
                .load(urlImagem)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.mImvFilme, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mProgressImagemFundo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public PepblastAdapterListener getPepblastAdapterListener() {
        return mPepblastAdapterListener;
    }

    public interface PepblastAdapterListener {

        void onButtonPlayClicked(View view);
        void onButtonDownloadClicked(View view);

    }

}
