package com.siva.insuris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.siva.insuris.model.Movies;
import com.siva.insuris.service.AppController;
import com.siva.insuris.utils.MyItemDecoration;
import com.siva.insuris.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvMovies;
    MoviesAdapter adapter;

    ArrayList<Movies.Results> movieResult=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovies = findViewById(R.id.rvMovies);

        rvMovies.setHasFixedSize(true);
        rvMovies.addItemDecoration(new DividerItemDecoration(rvMovies.getContext(), DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        rvMovies.setLayoutManager(mLayoutManager);


        if(Utils.isNetworkAvailable(MainActivity.this))
            getMoviesData("164e8ba217a4e0f5fcd603782cfea51c","en-US");
        else
            Toast.makeText(MainActivity.this,"Please check your internet connection..",Toast.LENGTH_SHORT).show();

    }

    private void getMoviesData(String appId, String lang) {
        AppController.getInstance().getApiManager().getMovies(appId,lang,"1")
                .enqueue(new Callback<Movies>() {
                    @Override
                    public void onResponse(Call<Movies> call, Response<Movies> response) {
                        if (response.code() == 200) {
                            ArrayList<Movies.Results> mMovie = response.body().results;
                            adapter = new MoviesAdapter(MainActivity.this, mMovie);
                            rvMovies.setAdapter(adapter);
                            rvMovies.scheduleLayoutAnimation();
                        } else if (response.code() == 400) {
                            Toast.makeText(MainActivity.this,"Failure 400",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this,"Failure 500",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Movies> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MainActivity.this,"Failure Error",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}