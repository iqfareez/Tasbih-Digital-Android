package com.maplerr.tasbihdigitalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private static final String S_MAIN_COUNT = "mainCount"; //utk savedInstanceState

    private static final String TAG = "MainActivity";
    private TextView countText;
    private TextView targetText;
    private Button buttonCount;
    private Button resetButton;
    private ProgressBar progressBar;

    public int countZikr = 0;
    public int targetZikr = 33;

    private long backPressedTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countText = findViewById(R.id.text_zikr);
        buttonCount = findViewById(R.id.button_count);
        resetButton = findViewById(R.id.button_reset);
        progressBar = findViewById(R.id.progressBar);
        targetText = findViewById(R.id.textView_progress_target);
        targetText.setText(String.valueOf(targetZikr));
        progressBar.setMax(targetZikr);

        countText.setText("0"); //initialize with zikir value


    }

    public void incrementCount(View view) {
        buttonCount.setText("+1");
        countZikr++;
        countText.setText(String.valueOf(countZikr));
        updateProgressBar();
        Log.i(TAG, "incrementCount: value is" + countZikr);

    }

    public void resetCount(View view) { //attached to reset button kat bawah tu
        countZikr = 0;
        countText.setText("0");

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progressBar.setProgress(0, true); //set progress bar balik ke 0
        } else {
            progressBar.setProgress(0); //no animation
        }
    }

    public void ViewAndroidBuildNum(View view) {
        Log.d(TAG, "ViewAndroidBuildNum: is" + VERSION.SDK_INT);
        //This is for debug purposes - attached with debug button
    }

    public void updateProgressBar(){

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progressBar.setProgress(countZikr, true);
        } else {
            progressBar.setProgress(countZikr); //no animation
        }

        //nnati try utk different api level
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(S_MAIN_COUNT, countZikr);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        countZikr = savedInstanceState.getInt(S_MAIN_COUNT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        countText.setText(String.valueOf(countZikr));
    }

    @Override
    public void onBackPressed() { //double tap to exit
        long millisToExit = 2000;

        if (backPressedTimer + millisToExit > System.currentTimeMillis()) {
            super.onBackPressed(); //finish
        } else {
            Toast.makeText(this, "Data will be loss! Tap again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTimer = System.currentTimeMillis();
    }
}
