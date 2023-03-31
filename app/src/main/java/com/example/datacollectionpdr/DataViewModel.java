package com.example.datacollectionpdr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;

public class DataViewModel extends ViewModel {
    private final MutableLiveData<MotionSample> motionSampleLiveData = new MutableLiveData<>();
    private final MutableLiveData<PDRStep> pdrStepLiveData = new MutableLiveData<>();

    public void updateMotionSample(MotionSample motionSample) {
        motionSampleLiveData.setValue(motionSample);
    }

    public void updatePDRSample(PDRStep pdrStep) {
        pdrStepLiveData.setValue(pdrStep);
    }

    public LiveData<MotionSample> getMotionSample() {
        return motionSampleLiveData;
    }

    public LiveData<PDRStep> getPDRStep() {
        return pdrStepLiveData;
    }
}
