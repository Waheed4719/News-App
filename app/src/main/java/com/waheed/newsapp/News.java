package com.waheed.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface News {

    @GET("everything")
    Call<NewsFeed> getNews(
       @Query("q") String q,
       @Query("apiKey") String API_KEY
    ) ;


    @GET("top-headlines")
    Call<NewsFeed> getHeads(
            @Query("country") String country,
            @Query("apiKey") String API_KEY

    ) ;


}
