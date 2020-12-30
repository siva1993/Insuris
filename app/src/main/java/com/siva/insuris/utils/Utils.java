package com.siva.insuris.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.siva.insuris.R;
import com.siva.insuris.service.NoConnectivityInterceptor;
import com.siva.insuris.service.NullStringToEmptyAdapterFactory;

import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by developer on 24/1/19.
 */

public class Utils {
    public static Dialog progressDialog1 = null;

    public static boolean isLollipopHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Get the Retrofit instance
     *
     * @return Retrofit
     */
    public static Retrofit getRetrofit(String baseDomain, Context context) {
        OkHttpClient okHttpClient = getUnsafeOkHttpClient(context);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseDomain)
                .build();
    }


    public static OkHttpClient getUnsafeOkHttpClient(Context context) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

//            OkHttpClient okHttpClient = builder.build();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            logging.setLevel(BuildConfig.isShowLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient okHttpClient = builder
//                    .addInterceptor(new NoConnectivityInterceptor(context))
                    .addInterceptor(logging)
                    .connectTimeout(20, TimeUnit.MINUTES)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS).build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * To check network connection available or not
     *
     * @return true if available else no, not available
     */
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo element : info) {
                    if (element.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void loadGlideImage(Context c, ImageView imgView, String strImage) {
        RequestOptions requestOptions = new RequestOptions();
//            requestOptions.transform(new GlideCropCircleTransformation(getActivity()));
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.error(R.drawable.ic_loading);
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(c)
                .load(strImage)
                .apply(requestOptions)
                .into(imgView);
    }

    public static void loadGlideImageBlur(Context c, ImageView imgView, String strImage) {
        RequestOptions bluOption = bitmapTransform(new BlurTransformation(25));
        bluOption.skipMemoryCache(true);
        bluOption.diskCacheStrategy(DiskCacheStrategy.NONE);
        bluOption.error(R.drawable.ic_loading);
        bluOption.placeholder(R.drawable.ic_loading);
        Glide.with(c)
                .load(strImage)
                .apply(bluOption)
                .into(imgView);
    }

    /**
     * Lollipop Animation
     *
     * @param window  getWindow()
     * @param context current instance context
     */
    public static void setupWindowAnimations(Window window, Context context) {
        if (Utils.isLollipopHigher()) {
            Slide enterTransition = new Slide();
            enterTransition.setSlideEdge(Gravity.END);
            enterTransition.setDuration(500);
            window.setEnterTransition(enterTransition);
        }
    }

    public static String getDate(String time) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

}
