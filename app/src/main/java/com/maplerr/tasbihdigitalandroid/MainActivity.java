package com.maplerr.tasbihdigitalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.maplerr.tasbihdigitalandroid.NotificationBuilder.CHANNEL_ID_1;

public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String S_MAIN_COUNT = "mainCount"; //utk SharedPreference
    private static final String S_PROG_COUNT = "progressCount"; //utk SharedPreference
    private static final String S_CUMMU_COUNT = "cummulativeCount"; //utk SharedPreference
    private static final String S_TARGET_ZIKR = "targetZikr"; //target zikir counter tepi progress bar
    private static final String S_TEXT_NAME = "UserNameText"; //name in text view

    private static final String TAG = "MainActivity";
    private TextView countText;
    private TextView cummulativeText;
    private TextView targetText;
    private Button buttonCount;
    private Button resetButton;
    private ProgressBar progressBar;
    private EditText nameText;

    public int countZikr = 0;
    public int targetZikr = 10;

    private int progressCounter = 0;
    private int cummulativeRound;

    private long backPressedTimer;

    public View parentLayout;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(R.id.parent_layout);

        notificationManager = NotificationManagerCompat.from(this);

        countText = findViewById(R.id.text_zikr);
        buttonCount = findViewById(R.id.button_count);
        resetButton = findViewById(R.id.button_reset);
        progressBar = findViewById(R.id.progressBar);
        targetText = findViewById(R.id.textView_progress_target);
        cummulativeText = findViewById(R.id.textView_cummulative_count);
        targetText.setText(String.valueOf(targetZikr));
        progressBar.setMax(targetZikr);
        nameText = findViewById(R.id.editTextPersonName);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isFirstStart = prefs.getBoolean("firstStart", true);

        if (isFirstStart)
            showWelcomeDialog();

        countText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyText(countText.getText());
                vibrateFeedback(55);
                showSnackBar(parentLayout, "Copied!");
                return true;
            }
        });

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCount();
            }
        });

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

        cummulativeText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyText(cummulativeText.getText());
                vibrateFeedback(55);
                showSnackBar(parentLayout, "Copied!");
                return true;
            }
        });

        nameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    nameText.clearFocus();
                    if (nameText.length() > 0) {
                        showSnackBar(parentLayout, "Name successfully set");
                    } else
                        showSnackBar(parentLayout, "Name cleared");
                }
                return false;
            }

        });

        
    }

    private void showWelcomeDialog() {
        WelcomeDialog welcomeDialog = new WelcomeDialog(this);
        welcomeDialog.show(getSupportFragmentManager(),"welcome dialog");

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

    private void copyText(CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", text);
        clipboard.setPrimaryClip(clip);

        //https://developer.android.com/guide/topics/text/copy-paste#java
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
            case R.id.action_item_4: //showNotifs
                showOnNotification();
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

        if (nameText.length() > 0) {
            message = message + " -" + nameText.getText();
        }

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

    public void incrementCount() { //also handling updating text view
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
            vibrateFeedback(170);
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
        editor.putString(S_TEXT_NAME, nameText.getText().toString());

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
        nameText.setText(prefs.getString(S_TEXT_NAME, ""));

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

    private void vibrateFeedback(long millis) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (v != null) { //for remove warning
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(millis);
            }
        }

        //https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
        //https://stackoverflow.com/questions/46957405/method-invocation-vibrate-may-produce-java-lang-nullpointerexception-warning-a
    }

    public void showOnNotification() {
        finish();
        String title = "Count from notification";

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("action", countZikr);

        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_notifs_icon)
                .setContentTitle(title)
                .setContentText(String.valueOf(countZikr))
                .setColor(Color.rgb(230, 28, 98))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_fluent_add_24_regular, "Count", actionIntent)
                .build();

        notificationManager.notify(1, notification);
    }

}
