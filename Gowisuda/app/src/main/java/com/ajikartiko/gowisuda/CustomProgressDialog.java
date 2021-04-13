package com.ajikartiko.gowisuda;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dinuscxj.progressbar.CircleProgressBar;

public class CustomProgressDialog extends AlertDialog {
    CircleProgressBar progressBar;
    TextView titleDialog;

    public CustomProgressDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
        progressBar = view.findViewById(R.id.progress);
        titleDialog = view.findViewById(R.id.progress_text);
        setContentView(view);

    }
}
