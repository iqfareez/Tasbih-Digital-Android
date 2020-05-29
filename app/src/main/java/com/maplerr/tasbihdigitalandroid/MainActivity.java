package com.maplerr.tasbihdigitalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity {
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
            case R.id.action_share:
                shareValueToOtherApp();
                return true;
            case R.id.action_item_1: //about
                openAboutDialog();
                return true;
            case R.id.action_subitem_1: //email
                openWebPage("mailto:foxtrotiqmal3@gmail.com");
                return true;
            case R.id.action_subitem_2: //website
                openWebPage("https://sites.google.com/view/tasbihdigitalfareez/home");
                return true;
            case R.id.action_subitem_3: //playstore app
                Toast.makeText(this, "You can promote this app to your friends", Toast.LENGTH_SHORT).show();
                openWebPage("https://play.google.com/store/apps/details?id=com.maplerr.tasbihdigitalandroid");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void shareValueToOtherApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String message; //customize message here
        if (countZikr == 0)
            message = "I didn't make any progress yet";
        else
            message = "I made till " + countZikr + ".";

        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
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

        resetButton.setVisibility(View.VISIBLE);

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
        resetButton.setVisibility(View.INVISIBLE);

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progressBar.setProgress(0, true); //set progress bar balik ke 0
        } else {
            progressBar.setProgress(0); //no animation
        }

        // TODO: 29/5/2020 Snackbar done reset

    }

    public void updateProgressBar() {

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

    public void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
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

        if (countZikr > 0) {
            buttonCount.setText("+1");
            resetButton.setVisibility(View.VISIBLE);
        }
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
