package com.siva.insuris.service;

import android.app.Application;

import com.siva.insuris.utils.Utils;


public class AppController extends Application {
    private static AppController INSTANCE;
    private ApiManager apiManager;
    private String baseDomain = "https://api.themoviedb.org/3/movie/";

    /**
     * Get the Application Instance
     *
     * @return
     */

    public static AppController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppController();
        }
        return INSTANCE;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    /**
     * Get the API Manager for calling API
     *
     * @return instance
     */


    public ApiManager getApiManager() {
        if (apiManager == null) {
            apiManager = Utils.getRetrofit(baseDomain, this).create(ApiManager.class);
        }
        return apiManager;
    }

}
