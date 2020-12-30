package com.siva.insuris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.siva.insuris.model.Movies;
import com.siva.insuris.model.MoviesDetailsModel;
import com.siva.insuris.model.RatingModel;
import com.siva.insuris.model.RatingRequest;
import com.siva.insuris.service.AppController;
import com.siva.insuris.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    TextView txtTitle,txtDate,txtOverview,txtVotes,txtTagline,txtDuration,txtGenre,txtProduction;
    ImageView imgMain,imgBack,imgHeader;
    private MaterialRatingBar ratingBar;

    String movieName,movieImage,movieDate,movieOverview,movieRating,movieId,movieBgImg;
    Boolean isLarge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        imgBack = findViewById(R.id.imgBack);
        imgMain = findViewById(R.id.imgMain);
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        txtOverview = findViewById(R.id.txtOverview);
        txtVotes = findViewById(R.id.txtVotes);
        txtTagline = findViewById(R.id.txtTagline);
        ratingBar = findViewById(R.id.rb_rating);
        txtDuration = findViewById(R.id.txtDuration);
        txtGenre = findViewById(R.id.txtGenre);
        txtProduction = findViewById(R.id.txtProduction);
        imgHeader = findViewById(R.id.imgHeader);
        Utils.setupWindowAnimations(getWindow(),MovieDetailsActivity.this);

        Intent intent = getIntent();
        movieId = intent.getStringExtra("movieId");
        movieName = intent.getStringExtra("movieName");
        movieImage = intent.getStringExtra("movieImage");
        movieDate = intent.getStringExtra("movieDate");
        movieOverview = intent.getStringExtra("movieOverview");
        movieRating = intent.getStringExtra("movieRating");
        movieBgImg = intent.getStringExtra("movieBannerImg");

        Utils.loadGlideImage(MovieDetailsActivity.this,imgMain,movieImage);
        Utils.loadGlideImageBlur(MovieDetailsActivity.this,imgHeader,movieBgImg);
        txtTitle.setText(movieName);
        txtOverview.setText(movieOverview);
        txtVotes.setText(movieRating);

        if(Utils.isLollipopHigher()){
            txtTitle.setTransitionName("movieName");
            imgMain.setTransitionName("movieImage");
        }

        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                float product = rating * 2;
                String strRatings = String.valueOf(product);
                Toast.makeText(MovieDetailsActivity.this,strRatings,Toast.LENGTH_SHORT).show();

                if(Utils.isNetworkAvailable(MovieDetailsActivity.this))
                      rateMovie(movieId,"164e8ba217a4e0f5fcd603782cfea51c",strRatings);
                else
                    Toast.makeText(MovieDetailsActivity.this,"Please check your internet connection..",Toast.LENGTH_SHORT).show();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(Utils.isNetworkAvailable(MovieDetailsActivity.this))
              getMoviesDetails(movieId,"164e8ba217a4e0f5fcd603782cfea51c","en-US");
        else
              Toast.makeText(MovieDetailsActivity.this,"Please check your internet connection..",Toast.LENGTH_SHORT).show();

        final ConstraintSet const1 = new ConstraintSet();
        final ConstraintSet const2 = new ConstraintSet();

        const2.clone(this,R.layout.activity_movie_details_big);

        final ConstraintLayout constrainLay = (ConstraintLayout) findViewById(R.id.constraint_layout);
        const1.clone(constrainLay);

        ImageView imgMain = (ImageView) findViewById(R.id.imgMain);
        imgMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isLollipopHigher())
                    TransitionManager.beginDelayedTransition(constrainLay);

                if(isLarge){
                    const1.applyTo(constrainLay);
                }
                else{
                    const2.applyTo(constrainLay);
                }
                isLarge = !isLarge;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getMoviesDetails(String movieId, String appId, String lang) {
        AppController.getInstance().getApiManager().getMoviesDetails(movieId,appId,lang)
                .enqueue(new Callback<MoviesDetailsModel>() {
                    @Override
                    public void onResponse(Call<MoviesDetailsModel> call, Response<MoviesDetailsModel> response) {
                        if (response.code() == 200) {
                            populateMovieDetails(response);
                        } else if (response.code() == 400) {
                            Toast.makeText(MovieDetailsActivity.this,"Failure 400",Toast.LENGTH_SHORT).show();
                            JSONObject jobjresponse = null;
                            try {
                                jobjresponse = new JSONObject(response.errorBody().string());
                                String status = jobjresponse.getString("error");
                                JSONObject obj = new JSONObject(status);
                                String msg = obj.getString("description");


                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else if (response.code() == 500) {
                            Toast.makeText(MovieDetailsActivity.this,"Failure 500",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesDetailsModel> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MovieDetailsActivity.this,"Failure Error",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void populateMovieDetails(Response<MoviesDetailsModel> response){
      txtTagline.setText(response.body().getTagline());
      if(response.body().getStatus().equals("Released"))
          txtDate.setText("Released on: "+Utils.getDate(response.body().getRelease_date()));
      else
          txtDate.setText("Release date: "+Utils.getDate(response.body().getRelease_date()));
      txtDuration.setText("Duration: "+getDuration(response.body().getRuntime()));

      //to get genre and production list
        List<String> genreList = new ArrayList<>();
        List<String> productionList = new ArrayList<>();
        String strGenre ="";
        String strProduction="";

        for(int genre = 0 ;genre < response.body().genres.size() ; genre++){
            genreList.add(response.body().genres.get(genre).getName());
        }
        for(int prod = 0 ;prod < response.body().production_companies.size() ; prod++){
            productionList.add(response.body().production_companies.get(prod).getName());
        }

        if(genreList.size() > 0){
            if (genreList.size() == 1) {
                strGenre = genreList.get(0);
            } else {
                StringBuilder csvBuilder = new StringBuilder();
                for (String genre : genreList) {
                    csvBuilder.append(genre);
                    csvBuilder.append(",");
                }
                String poleIds = csvBuilder.toString();
                strGenre = removeLastChar(poleIds);
            }

            txtGenre.setText(strGenre);
        }

       if(productionList.size() > 0){
           if (productionList.size() == 1) {
               strProduction = productionList.get(0);
           } else {
               StringBuilder csvBuilder = new StringBuilder();
               for (String prod : productionList) {
                   csvBuilder.append(prod);
                   csvBuilder.append(",");
               }
               String poleIds = csvBuilder.toString();
               strProduction = removeLastChar(poleIds);
           }

           txtProduction.setText(strProduction);
       }

    }

    private String getDuration(int runtime){
        String duration = "";
        int hours = runtime / 60; //since both are ints, you get an int
        int minutes = runtime % 60;
        System.out.printf("%d:%02d", hours, minutes);
        duration = hours+"h "+minutes+"mins";
        return duration;
    }

    public String removeLastChar(String str) {
        return removeLastChars(str, 1);
    }
    public static String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }

    private void rateMovie(String movieId, String appId, String rate) {
        RatingRequest request = new RatingRequest();
        request.setValue(rate);

        AppController.getInstance().getApiManager().rateMovie(movieId,appId,request)
                .enqueue(new Callback<RatingModel>() {
                    @Override
                    public void onResponse(Call<RatingModel> call, Response<RatingModel> response) {
                        if (response.code() == 200) {
                            String msg = response.body().getStatus_message();
                            Toast.makeText(MovieDetailsActivity.this,msg,Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else if (response.code() == 400 || response.code() == 401) {
//                            String msg = response.body().getStatus_message();
                            Toast.makeText(MovieDetailsActivity.this,"Authentication failed: You do not have permissions to access the service",Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(MovieDetailsActivity.this,"Failure 500",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RatingModel> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MovieDetailsActivity.this,"Failure Error",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}