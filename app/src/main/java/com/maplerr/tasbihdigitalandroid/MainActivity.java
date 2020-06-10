package com.maplerr.tasbihdigitalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String S_MAIN_COUNT = "mainCount"; //utk SharedPreference
    private static final String S_PROG_COUNT = "progressCount"; //utk SharedPreference
    private static final String S_CUMMU_COUNT = "cummulativeCount"; //utk SharedPreference
    private static final String S_TARGET_ZIKR = "targetZikr"; //target zikir counter tepi progress bar

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

    public View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(R.id.parent_layout);

        countText = findViewById(R.id.text_zikr);
        buttonCount = findViewById(R.id.button_count);
        resetButton = findViewById(R.id.button_reset);
        progressBar = findViewById(R.id.progressBar);
        targetText = findViewById(R.id.textView_progress_target);
        cummulativeText = findViewById(R.id.textView_cummulative_count);
        targetText.setText(String.valueOf(targetZikr));
        progressBar.setMax(targetZikr);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countZikr != 0)
                    openResetDialog();
            }
        });

        targetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTargetDialog(); //set target dialog boleh dibuka dengan teka kat atas or kt number
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
            case R.id.action_item_3: //setTargetValue
                openTargetDialog();
                return true;
            case R.id.action_subitem_1: //email
                openWebPage("mailto:foxtrotiqmal3@gmail.com");
                return true;
            case R.id.action_subitem_2: //website
                openWebPage("https://sites.google.com/view/tasbihdigitalfareez/home");
                return true;
            case R.id.action_subitem_3: //playstore app
                Toast.makeText(this, "You can promote this app other people", Toast.LENGTH_LONG).show();
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
        message = "As of " + getCurrentDateTime() + ", ";
        if (countZikr == 0)
            message = message + "I didn't make any progress yet";
        else
            message = message + "I made till " + countZikr + ".";

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

    public void resetCount(Boolean proceed) { //attached to reset button kat bawah tu
        if (proceed) {
            countZikr = 0;
            progressCounter = 0;
            countText.setText("0");
            buttonCount.setText("START");
            cummulativeRound = 0;
            cummulativeText.setText("");
            resetButton.setVisibility(View.INVISIBLE);

            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                progressBar.setProgress(0, true); //set progress bar balik ke 0
            } else {
                progressBar.setProgress(0); //no animation
            }

            showSnackBar(parentLayout, "Reset done");
        }
        else
            showSnackBar(parentLayout,"Canceled. Nothing changed");

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

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(S_MAIN_COUNT, countZikr);
        editor.putInt(S_PROG_COUNT, progressCounter);
        editor.putInt(S_CUMMU_COUNT, cummulativeRound);
        editor.putInt(S_TARGET_ZIKR, targetZikr);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        countZikr = prefs.getInt(S_MAIN_COUNT, 0);
        progressCounter = prefs.getInt(S_PROG_COUNT, 0);
        cummulativeRound = prefs.getInt(S_CUMMU_COUNT, 0);
        targetZikr = prefs.getInt(S_TARGET_ZIKR, 10);

        progressBar.setMax(targetZikr);
        updateProgressBar();

        targetText.setText(String.valueOf(targetZikr));

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

    public void openTargetDialog() {
        SetTargetPicker newFragment = new SetTargetPicker();
        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "target picker");
    }

    public void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private String getCurrentDateTime() {
        String pattern = "dd/MM/yy HH:mm:ss";
        //result: 03/06/20 05:19:15
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        return date;
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

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        if (oldVal != newVal) {
            showSnackBar(parentLayout, "Target number changed to " + newVal);
            targetZikr = newVal;
            targetText.setText(String.valueOf(targetZikr));
            progressBar.setMax(targetZikr);
            cummulativeRound = progressCounter = 0;
            cummulativeText.setText("0");
        } else {
            showSnackBar(parentLayout,"Nothing changed. Target value is " + oldVal);
        }

    }

    private void vibrateFeedback(int millis) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

}
