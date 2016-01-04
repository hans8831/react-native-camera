package com.baebae.reactnativecamera;

import android.support.annotation.Nullable;

import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.ReactApplicationContext;

public class CameraViewManager extends SimpleViewManager<CameraView> {
    public static final String REACT_CLASS = "CameraViewAndroid";
    private ReactApplicationContext reactApplicationContext = null;
    private CameraView cameraView = null;
    public CameraViewManager(ReactApplicationContext reactApplicationContext) {
        this.reactApplicationContext = reactApplicationContext;
    }
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected CameraView createViewInstance(ThemedReactContext context) {
        cameraView = new CameraView(context);
        cameraView.startCamera();
        return cameraView;
    }

    @ReactProp(name = "fileName")
    public void setFileName(CameraView view, @Nullable String fileName) {
        view.setCapturedFileName(fileName);
    }

    @ReactProp(name = "startCapture")
    public void startCapture(CameraView view, @Nullable String flagValue) {
        if (flagValue.equals("true")) {
            view.takePicture();
        }
    }

    @Override
    public void updateExtraData(CameraView view, Object props) {
        super.updateExtraData(view, props);
    }
}