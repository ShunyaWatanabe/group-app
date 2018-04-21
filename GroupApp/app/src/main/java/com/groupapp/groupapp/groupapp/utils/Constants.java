package com.groupapp.groupapp.groupapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.android.gms.nearby.connection.Strategy;
import com.groupapp.groupapp.groupapp.model.User;

/**
 * Created by Tomek on 2018-04-04.
 */

public class Constants {

    public static final String BASE_URL = "https://group-app-android.herokuapp.com"; //"http://192.168.0.21:8080"; //"https://ballot-box-test.herokuapp.com";
    public static final String API_URL = "https://group-app-android.herokuapp.com/api/v1/"; //"http://192.168.0.21:8080/api/v1/"; //"https://ballot-box-test.herokuapp.com/api/v1/";
    //"https://ballot-box-test.herokuapp.com/api/v1/";
    // "http://192.168.1.108:8080/api/v1/";
    public static final String TOKEN = "token";
    public static final String NAME = "name";
    public static final String PRIVATE_KEY = "private_key";
    public static final String REFRESH_TOKEN = "refresh_token";
//    public static final String COGNITO_POOL_ID = "us-east-1:20a9c264-b671-47a6-8698-7bda9ea6fdfa";
//    public static final String COGNITO_POOL_REGION = "us-east-1";
//    public static final String BUCKET_GROUPS = "ballotbox-debates";
//    public static final String USER_PICTURE_BUCKET = "https://s3.amazonaws.com/ballotbox-users/";


    public static final String dirPathMain = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroupApp/";
    public static final String dirPathMyPics= dirPathMain + "GroupAppPics/";

    public static final int GALLERY_PERMISSIONS_REQUEST = 0;
    public static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final int LOCATION_REQUEST = 4;
    public static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    public static User loggedUser;

    private static Context context;

    public static void saveTokens(Activity activity, String token, String refreshToken, String name, String private_key){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.putString(Constants.NAME, name);
        editor.putString(Constants.PRIVATE_KEY, private_key);
        editor.putString(Constants.REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public static void saveRefreshToken(Activity activity, String refreshToken){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public static void saveAccessToken(Activity activity, String token){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }

    public static void saveName(Activity activity, String name){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.NAME, name);
        editor.apply();
    }

    public static void savePrivateKey(Activity activity, String private_key){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.NAME, private_key);
        editor.apply();
    }

    public static String getAccessToken(Activity activity){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return mSharedPreferences.getString(Constants.TOKEN,"");
    }

    public static String getRefreshToken(Activity activity){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return mSharedPreferences.getString(Constants.REFRESH_TOKEN,"");
    }

    public static String getName(Activity activity){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return mSharedPreferences.getString(Constants.NAME,"");
    }

    public static String getPrivateKey(Activity activity){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return mSharedPreferences.getString(Constants.PRIVATE_KEY,"");
    }

    public static void resetTokens(Activity activity){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.NAME,"");
        editor.putString(Constants.TOKEN,"");
        editor.putString(Constants.REFRESH_TOKEN,"");
        editor.apply();
    }
}
