package com.example.coworkingillini.ui.invitations_received;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReceivedViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReceivedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}