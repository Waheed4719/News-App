package com.waheed.newsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.waheed.newsapp.ApiClient.getUnsafeOkHttpClient;

public class Home extends AppCompatActivity {

    private View decorView;
    GlobalFunctions uiBars = new GlobalFunctions();
    private RecyclerView recycler;
    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static final String q = "bitcoin";
    public static String country = "US";
    private static final String API_KEY = "6db5c47243d245a6a3493b44a0f3c82f";
    private List<Articles> articles = new ArrayList<>();
    private RecyclerAdapter adapter;
    private ProgressBar progressBar;
    private TextView newsLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);



        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setNestedScrollingEnabled(false);

        progressBar = findViewById(R.id.progressHome);
        newsLabel = findViewById(R.id.newsLabel);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0){
                    decorView.setSystemUiVisibility(uiBars.hideSystemBars());
                }
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        loadJson("");


    }

    public void loadJson(String keyword){

        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getUnsafeOkHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        News news = retrofit.create(News.class);

        country = Utils.getCountry();
        String language = Utils.getLanguage();
        Call<NewsFeed> call;
        if(keyword.length() > 0) {
            newsLabel.setText("Search Results");
            call = news.getNews(keyword,language,API_KEY);
        }
        else{
            newsLabel.setText("Top Headlines");
            call = news.getHeads(country,API_KEY);
        }



        call.enqueue(new Callback<NewsFeed>() {
            @Override
            public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response) {

                if(response.isSuccessful() && response.body().getArticles() != null){

                    if(!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    adapter = new RecyclerAdapter(articles, getApplicationContext());
                    recycler.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.length() > 2){
                    loadJson(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadJson(newText);
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false,false);
        return true;

    }








    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(uiBars.hideSystemBars());
        }
    }

}
