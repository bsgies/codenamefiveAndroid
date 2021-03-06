package com.itridtechnologies.codenamefive.UIViews.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.itridtechnologies.codenamefive.R;

import org.jetbrains.annotations.NotNull;

public class FragNoInternetDialog extends DialogFragment {

    Button mButtonCancel;
    Button mButtonOkay;
    private ConnectionErrorDialogListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connection_error_dialog, container, false);

        //find views
        mButtonCancel = view.findViewById(R.id.mat_btn_cancel);
        mButtonOkay = view.findViewById(R.id.mat_button_okay);

        mButtonOkay.setOnClickListener(v -> {
            mListener.onDialogButtonClicked(1);
            dismiss();
        });

        mButtonCancel.setOnClickListener(v -> {
            mListener.onDialogButtonClicked(0);
            dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ConnectionErrorDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    public interface ConnectionErrorDialogListener {
        void onDialogButtonClicked(int actionCode);
    }
}
