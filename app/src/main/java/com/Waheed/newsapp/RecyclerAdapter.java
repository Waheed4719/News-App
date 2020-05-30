package com.Waheed.newsapp;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

    List<Articles> news;
    Context mContext;
    OnItemClickListener onItemClickListener;

    public RecyclerAdapter(List<Articles> news, Context mContext) {
        this.news = news;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerAdapter.RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_layout,parent,false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view, onItemClickListener);

        return recyclerHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, int position) {

        Articles articles = news.get(position);
        holder.author.setText(articles.getAuthor());
        holder.title.setText(articles.getTitle());
        holder.desc.setText(articles.getDescription());
        holder.source.setText(articles.getSource().getName());
        holder.releaseDate.setText(Utils.DateFormat(articles.getPublishedAt()));
        holder.time.setText(" \u2022 " + Utils.DateToTimeFormat(articles.getPublishedAt()));


        holder.desc.setTypeface(setTF("poppins.ttf"));
        holder.title.setTypeface(setTF("poppins_medium.ttf"));
        holder.author.setTypeface(setTF("poppins_semibold.ttf"));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();


        Glide.with(mContext).load(articles.getUrlToImage())
                .apply(requestOptions)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.coverImage);


    }

    @Override
    public int getItemCount() {
        return news.size();
    }


    public Typeface setTF(String font){


        Typeface myTypeFace = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/"+font);
        return myTypeFace;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(View View, int position);
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title,author,releaseDate,desc,source,time;
        ImageView coverImage;
        ProgressBar progress;
        OnItemClickListener onItemClickListener;
        public RecyclerHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            desc = itemView.findViewById(R.id.desc);
            releaseDate = itemView.findViewById(R.id.releaseDate);
            source = itemView.findViewById(R.id.source);
            coverImage = itemView.findViewById(R.id.coverImage);
            progress = itemView.findViewById(R.id.progress);
            time = itemView.findViewById(R.id.time);

            this.onItemClickListener = onItemClickListener;
        }


        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v,getAdapterPosition());
        }
    }


}
