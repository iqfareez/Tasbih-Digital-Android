package com.maplerr.tasbihdigitalandroid;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class WelcomeDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //TODO: Setup welcoming dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.create();
    }
}
