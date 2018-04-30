package com.groupapp.groupapp.groupapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.screens.LogInFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class LogInFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity testingActivity;
    private LogInFragment testFragment;

    public LogInFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using
        // the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        testingActivity = getActivity();

        testFragment = new LogInFragment();
        testingActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragmentFrame, testFragment, null).commit();
        /**
         * Synchronously wait for the application to be idle.  Can not be called
         * from the main application thread -- use {@link #start} to execute
         * instrumentation in its own thread.
         *
         * Without waitForIdleSync(); our test would have nulls in fragment references.
         */
        getInstrumentation().waitForIdleSync();
    }

    public void testEditText() {
        String empty = "";
        EditText editText = testFragment.getView().findViewById(R.id.et_user_name);
        assertEquals("Empty stuff", true, editText.getText().equals(empty));
    }

    public void testButton(){
        Button button = testFragment.getView().findViewById(R.id.b_start);
        assertEquals("Button visible", true, button.isCursorVisible());
    }
}