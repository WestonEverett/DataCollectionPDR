package com.example.datacollectionpdr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;

import java.util.ArrayList;

public class DataViewModel extends ViewModel {
    private final MutableLiveData<MotionSample> motionSampleLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<PDRStep>> pdrStepLiveData = new MutableLiveData<>();

    public void updateMotionSample(MotionSample motionSample) {
        motionSampleLiveData.setValue(motionSample);
    }

    public void updatePDRSample(ArrayList<PDRStep> pdrSteps) {
        pdrStepLiveData.setValue(pdrSteps);
    }

    public LiveData<MotionSample> getMotionSample() {
        return motionSampleLiveData;
    }

    public LiveData<ArrayList<PDRStep>> getPDRStep() {
        return pdrStepLiveData;
    }
}
