package com.maplerr.tasbihdigitalandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import static com.maplerr.tasbihdigitalandroid.BuildConfig.VERSION_NAME;

public class AboutDialog extends AppCompatDialogFragment {

    private Context context;

    public AboutDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("About")
                .setIcon(R.drawable.ic_info)
                .setMessage("Digital Counter app is maplerr's project build because he bored in lockdown\n\n" +
                        "Version " + VERSION_NAME)
                .setPositiveButton("CLOSE", null)
                .setNeutralButton("SOCMED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).openWebPage("https://linktr.ee/iqFareez");
                        Toast.makeText(context, "Follow me lol :D", Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}
