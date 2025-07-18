package com.dreamprogramming.secuher.ui.chatbot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatbotViewModal extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChatbotViewModal() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ChatBot fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}