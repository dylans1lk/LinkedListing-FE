// This is the INVENTORY VIEW MODEL aka the place a corresponding fragment file goes to get
// the data (text, pics, etc) for the page.
package com.app.linkedlisting.ui.listings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    // This text is temporarily here to ensure the correct pages are being navigated to.
    public ListingViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}