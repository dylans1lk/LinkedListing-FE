// This is the LISTINGS VIEW MODEL aka the place a corresponding fragment file goes to get
// the data (text, pics, etc) for the page.
package com.app.linkedlisting.ui.listings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    // This text is temporarily here to ensure the correct pages are being navigated to.
    public ListingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is listings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}