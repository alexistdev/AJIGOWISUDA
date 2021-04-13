package com.ajikartiko.gowisuda;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

public class CustomTextInputDialog extends AppCompatDialogFragment {
    private String label, title, valueStart;
    private Integer inputType;
    private TextInputEditText textInput;
    private DialogListener listener;
    AlertDialog.Builder builder;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_edit_text_dialog, null);
        TextInputLayout inputLayout = view.findViewById(R.id.inputLayout);
        textInput = view.findViewById(R.id.textInput);
        inputLayout.setHint(label);
        textInput.setText(valueStart);
        textInput.requestFocus();
        if (inputType!=null){
            textInput.setInputType(inputType);
        }
        builder.setView(view)
                .setTitle(title)
                .setNegativeButton("cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("ok", (dialogInterface, i) -> listener.applyTexts(textInput.getText().toString().trim()));

        return builder.create();
    }

    public void setTextInput(String label, String value, Integer inputType) {
        this.label = label;
        this.valueStart = value;
        this.inputType = inputType;
    }

    public void setTitle(String value) {
        title = value;
    }


    public void setListener(DialogListener dialogListener) {
        listener = dialogListener;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            if (listener==null){
                listener = (DialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void applyTexts(String result);
    }
}
