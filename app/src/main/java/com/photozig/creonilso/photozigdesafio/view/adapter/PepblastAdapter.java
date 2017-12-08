package com.photozig.creonilso.photozigdesafio.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photozig.creonilso.photozigdesafio.R;
import com.photozig.creonilso.photozigdesafio.model.Filme;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private List<Boolean> mProgressList;
    private PepblastAdapterListener mPepblastAdapterListener;

    public PepblastAdapter(PepblastAdapterListener pepblastAdapterListener) {
        this.mPepblastAdapterListener = pepblastAdapterListener;
        mDataset = new ArrayList<>();
        mProgressList = new ArrayList<>();
    }

    public void removeProgressDownload(int posicao) {
        mProgressList.set(posicao, false);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_nome)
        TextView mTextView;
        @BindView(R.id.img_filme)
        ImageView mImvFilme;
        @BindView(R.id.progress_imagem_fundo)
        ProgressBar mProgressImagemFundo;
        @BindView(R.id.btn_baixar)
        Button mBtnBaixar;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.btn_baixar)
        public void onButtonDownloadClicked(View view){
            mProgressImagemFundo.setVisibility(View.VISIBLE);
            mProgressList.set(getAdapterPosition(), true);
            view.setEnabled(false);
            PepblastAdapter.this.getPepblastAdapterListener().onButtonDownloadClicked(view, mDataset.get(getAdapterPosition()), getAdapterPosition());
        }

        @OnClick(R.id.btn_visualizar)
        public void onButtonPlayClicked(View view){
            PepblastAdapter.this.getPepblastAdapterListener().onButtonPlayClicked(view, mDataset.get(getAdapterPosition()));
        }

    }

    public void setDataset(List<Filme> myDataset) {
        mDataset = myDataset;
        mProgressList = new ArrayList<>(Arrays.asList(new Boolean[mDataset.size()]));
        Collections.fill(mProgressList, Boolean.FALSE);
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
        final String urlImagem = PEPBLAST_BASE_URL + PEPBLAST_ASSETS_PATH + mDataset.get(position).getImagem();
        if(mProgressList.get(position)){
            holder.mProgressImagemFundo.setVisibility(View.VISIBLE);
        } else {
            holder.mProgressImagemFundo.setVisibility(View.GONE);
            holder.mBtnBaixar.setEnabled(true);
        }
        Picasso picasso = Picasso.with(holder.mImvFilme.getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.load(urlImagem).error(android.R.drawable.presence_offline)
                .into(holder.mImvFilme, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mProgressImagemFundo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Log.d("debug", "erro download imagem");
                        holder.mProgressImagemFundo.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public PepblastAdapterListener getPepblastAdapterListener() {
        return mPepblastAdapterListener;
    }



    public interface PepblastAdapterListener {

        void onButtonPlayClicked(View view, Filme filme);
        void onButtonDownloadClicked(View view, Filme filme, int posicao);

    }

}
