package com.maplerr.tasbihdigitalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private static final String S_MAIN_COUNT = "mainCount"; //utk SharedPreference
    private static final String S_PROG_COUNT = "progressCount"; //utk SharedPreference
    private static final String S_CUMMU_COUNT = "cummulativeCount"; //utk SharedPreference

    private static final String TAG = "MainActivity";
    private TextView countText;
    private TextView cummulativeText;
    private TextView targetText;
    private Button buttonCount;
    private Button resetButton;
    private ProgressBar progressBar;

    public int countZikr = 0;
    public int targetZikr = 10;

    private int progressCounter = 0;
    private int cummulativeRound;

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
        cummulativeText = findViewById(R.id.textView_cummulative_count);
        targetText.setText(String.valueOf(targetZikr));
        progressBar.setMax(targetZikr);

//        countText.setText("0"); //initialize with zikir value

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countZikr != 0)
                    openResetDialog();
                else
                    Toast.makeText(MainActivity.this, "Counter is already 0", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //region menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //add action2 kau kat sini
        switch (item.getItemId()) {
            case R.id.action_more_menu:
                Toast.makeText(this, "More action menu clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_item_1:
                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_item_2:
                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_subitem_1:
                Toast.makeText(this, "Subitem 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //endregion

    public void incrementCount(View view) { //alsp handling updating text view
        buttonCount.setText("+1");
        countZikr++;
        countText.setText(String.valueOf(countZikr));
        progressCounter++;
        updateProgressBar();
//        Log.i(TAG, "incrementCount: value is" + countZikr + "progressCount is " + progressCounter);

        if (progressCounter == targetZikr) {
            progressCounter = 0;
            cummulativeRound += 1;
            cummulativeText.setText("Round: " + cummulativeRound);
        }
    }

    public void resetCount() { //attached to reset button kat bawah tu
        countZikr = 0;
        progressCounter = 0;
        countText.setText("0");
        buttonCount.setText("START");
        cummulativeRound = 0;
        cummulativeText.setText("0");

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progressBar.setProgress(0, true); //set progress bar balik ke 0
        } else {
            progressBar.setProgress(0); //no animation
        }

        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();

    }

    public void updateProgressBar(){

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progressBar.setProgress(progressCounter, true);
        } else {
            progressBar.setProgress(progressCounter); //no animation
        }

        //nnati try utk different api level
    }

    public void openResetDialog() {
        ResetDialog resetDialog = new ResetDialog(this);
        resetDialog.show(getSupportFragmentManager(), "reset dialog");
    }

    /* OnSaveInstance function
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(S_MAIN_COUNT, countZikr);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        countZikr = savedInstanceState.getInt(S_MAIN_COUNT);

        if (countZikr > 0)
            buttonCount.setText("+1");
    }
    */

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(S_MAIN_COUNT, countZikr);
        editor.putInt(S_PROG_COUNT, progressCounter);
        editor.putInt(S_CUMMU_COUNT, cummulativeRound);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        countZikr = prefs.getInt(S_MAIN_COUNT, 0);
        progressCounter = prefs.getInt(S_PROG_COUNT, 0);
        cummulativeRound = prefs.getInt(S_CUMMU_COUNT, 0);

        updateProgressBar();
        cummulativeText.setText("Round: " + cummulativeRound);

        if (countZikr > 0)
            buttonCount.setText("+1");
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
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTimer = System.currentTimeMillis();
    }

    //DEBUG ONLY
    public void ViewAndroidBuildNum(View view) {
        Log.d(TAG, "ViewAndroidBuildNum: is" + VERSION.SDK_INT);
        //This is for debug purposes - attached with debug button
    }

    // TODO: 22/5/2020 Disable buttonReset when value = 0, toolbar icon showing app info, set target

}
