package com.waheed.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity {

    private View decorView;
    GlobalFunctions uiBars = new GlobalFunctions();
    private RecyclerView recycler;
    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static final String q = "bitcoin";
    public static final String country = "US";
    private static final String API_KEY = "bb1075a65c2e40ab8812092345615362";
    private List<Articles> articles;
    private RecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setNestedScrollingEnabled(false);


        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0){
                    decorView.setSystemUiVisibility(uiBars.hideSystemBars());
                }
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        News news = retrofit.create(News.class);


        loadJson(news);


    }

    public void loadJson(News news){
        Call<NewsFeed> call = news.getNews(country,API_KEY);


        call.enqueue(new Callback<NewsFeed>() {
            @Override
            public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response) {

                if(response.isSuccessful() && response.body().getArticles() != null){


                    articles = response.body().getArticles();
                    adapter = new RecyclerAdapter(articles, getApplicationContext());
                    recycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View View, int position) {
                            Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Home.this,NewsDetailActivity.class);

                            Articles newArticle = articles.get(position);
                            intent.putExtra("MyClass",  newArticle);
                            startActivity(intent);

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<NewsFeed> call, Throwable t) {

            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(uiBars.hideSystemBars());
        }
    }

}
