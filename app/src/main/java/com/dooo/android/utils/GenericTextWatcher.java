package com.dooo.android.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


public class GenericTextWatcher implements TextWatcher {
    private EditText currentView;
    private EditText nextView;

    public GenericTextWatcher(EditText currentView, EditText nextView) {
        this.currentView = currentView;
        this.nextView = nextView;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // TODO Auto-generated method stub
        String text = editable.toString();
        if (nextView != null && text.length() == 1) {
            nextView.requestFocus();
        }
        if(text.length() >1){
            currentView.setText(String.valueOf(text.charAt(text.length() - 1)));
            currentView.setSelection(1);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }
}
