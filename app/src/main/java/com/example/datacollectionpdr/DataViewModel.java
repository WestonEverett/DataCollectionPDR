package com.example.datacollectionpdr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.PressureData;

import java.util.ArrayList;

/** class to allow access of sensor and pdr data in recording activity UI*/
public class DataViewModel extends ViewModel {
    private final MutableLiveData<MotionSample> motionSampleLiveData = new MutableLiveData<>(); //variablre to pass motion samples
    private final MutableLiveData<PDRStep> pdrStepLiveData = new MutableLiveData<>();   //variable to pass pdr steps
    private final MutableLiveData<PressureData> pressureLiveData = new MutableLiveData<>(); //variable to pass pressure values

    /** give value of the last motion sample*/
    public void updateMotionSample(MotionSample motionSample) {
        motionSampleLiveData.setValue(motionSample);
    }

    /** give value of the last pdr step*/
    public void updatePDRSample(PDRStep pdrStep) {
        pdrStepLiveData.setValue(pdrStep);
    }

    /** initialise variable to store motion sample*/
    public LiveData<MotionSample> getMotionSample() {
        return motionSampleLiveData;
    }

    /** initialise variable to store pdr steps*/
    public LiveData<PDRStep> getPDRStep() {
        return pdrStepLiveData;
    }

    /** give value of the last pressure sample*/
    public void updatePressure(PressureData pressureData) {
        pressureLiveData.setValue(pressureData);
    }

    /** initialise variable to store pressure measurements*/
    public LiveData<PressureData> getPressure() {
        return pressureLiveData;
    }
}
