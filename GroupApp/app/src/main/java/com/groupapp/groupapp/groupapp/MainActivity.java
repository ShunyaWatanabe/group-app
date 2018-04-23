package com.groupapp.groupapp.groupapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.groupapp.groupapp.groupapp.screens.ChatPageFragment;
import com.groupapp.groupapp.groupapp.screens.CreateGroupFragment;
import com.groupapp.groupapp.groupapp.screens.GroupsListFragment;
import com.groupapp.groupapp.groupapp.screens.LogInFragment;
import com.groupapp.groupapp.groupapp.screens.JoinGroupFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment();
    }

    private void replaceFragment() {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("MainActivity");
        LogInFragment fragment = new LogInFragment();
        ft.replace(R.id.fragmentFrame, fragment, LogInFragment.TAG);
        ft.commit();
        Log.e("Stack count", getSupportFragmentManager().getBackStackEntryCount() + "");
    }

    @Override
    public void onBackPressed() {
        // Catch back action and pops from backstack
        // (if you called previously to addToBackStack() in your transaction)
        Log.e("Stack count", getSupportFragmentManager().getBackStackEntryCount() + "");
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        // Default action on back pressed
        else {
            super.onBackPressed();
            this.finishAffinity();
        }
    }
}
