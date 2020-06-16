package com.maplerr.tasbihdigitalandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SetTargetPicker extends DialogFragment {
    private static final String TAG = "SetTargetPicker";
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final NumberPicker numberPicker = new NumberPicker(getActivity());

        final int currentVal = ((MainActivity)getActivity()).targetZikr;
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(5);
        numberPicker.setValue(currentVal);

        Log.d(TAG, "onCreateDialog: curretVal=" + currentVal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set target between 5 to 100");
        builder.setMessage("Round count will be reset!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker, currentVal, numberPicker.getValue());
                //TODO maybe try to listen input value before commit
                Log.d(TAG, "onClick: currentVal=" + currentVal + ", newVal=" + numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).showSnackBar(((MainActivity)getActivity()).parentLayout, "Nothing changed");
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
