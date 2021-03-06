package com.groupapp.groupapp.groupapp.network;

import android.util.Base64;

import com.groupapp.groupapp.groupapp.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class NetworkUtil {

    public static RetrofitInterface getRetrofit(){

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);

    }

//    public static RetrofitInterface getRetrofit(String private_key, String password) {
//
//        String credentials = private_key + ":" + password;
//        String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//
//        httpClient.addInterceptor(chain -> {
//
//            Request original = chain.request();
//            Request.Builder builder = original.newBuilder()
//                    .addHeader("Authorization", basic)
//                    .method(original.method(),original.body());
//            return  chain.proceed(builder.build());
//
//        });
//
//        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
//
//        return new Retrofit.Builder()
//                .baseUrl(Constants.API_URL)
//                .client(httpClient.build())
//                .addCallAdapterFactory(rxAdapter)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build().create(RetrofitInterface.class);
//    }

    public static RetrofitInterface getRetrofit(String token, String refreshToken, String private_key) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .addHeader("x-access-token", token)
                    .addHeader("refresh-token", refreshToken)
                    .addHeader("private_key", private_key)
                    .method(original.method(),original.body());
            return  chain.proceed(builder.build());

        });

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);
    }
}
