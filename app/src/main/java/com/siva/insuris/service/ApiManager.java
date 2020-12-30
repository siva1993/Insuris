package com.siva.insuris.service;


import com.siva.insuris.model.Movies;
import com.siva.insuris.model.MoviesDetailsModel;
import com.siva.insuris.model.RatingModel;
import com.siva.insuris.model.RatingRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by HP2 on 4/2/2018.
 */

public interface ApiManager {

    @GET("popular")
    Call<Movies> getMovies(@Query("api_key") String key, @Query("language") String lang, @Query("page") String page);

    @GET("{id}")
    Call<MoviesDetailsModel> getMoviesDetails(@Path("id") String id, @Query("api_key") String key, @Query("language") String lang);

    @POST("{id}/rating")
    Call<RatingModel> rateMovie(@Path("id") String siteId, @Query("api_key") String key,@Body RatingRequest request);
}
