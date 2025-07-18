package com.dreamprogramming.secuher.ui.home;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();
    public LiveData<String> getText() {
        return mText;
    }

    public void updateText(String text) {
        mText.setValue(text);
    }
}