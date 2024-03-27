// This is the PROFILE VIEW MODEL aka the place a corresponding fragment file goes to get
// the data (text, pics, etc) for the page.
package com.app.linkedlisting.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    // This text is temporarily here to ensure the correct pages are being navigated to.
    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}