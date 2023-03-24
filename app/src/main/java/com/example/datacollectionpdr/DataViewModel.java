package com.example.datacollectionpdr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datacollectionpdr.nativedata.MotionSample;

public class DataViewModel extends ViewModel {
    private final MutableLiveData<MotionSample> selectedItem = new MutableLiveData<>();

    public void updateSample(MotionSample motionSample) {
        selectedItem.setValue(motionSample);
    }

    public LiveData<MotionSample> getSample() {
        return selectedItem;
    }
}
