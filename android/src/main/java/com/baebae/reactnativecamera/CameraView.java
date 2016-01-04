package com.baebae.reactnativecamera;


import com.baebae.reactnativecamera.cameralib.helpers.FileUtils;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;
import com.baebae.reactnativecamera.cameralib.ui.CameraPreviewLayout;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
public class CameraView extends CameraPreviewLayout implements LifecycleEventListener{

    public CameraView(ThemedReactContext context) {
        super(context);
        context.addLifecycleEventListener(this);
    }

    private final Runnable mLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(mLayoutRunnable);
    }

    @Override
    public void onHostResume() {
        startCamera();
    }

    @Override
    public void onHostPause() {
        stopCamera();
    }

    @Override
    public void onHostDestroy() {
        stopCamera();
    }

    public void setCapturedFileName(String fileName) {
        captureFileName = FileUtils.getApplicationDirectory() + fileName + ".jpg";
    }

    public String getCapturedFileName() {
        return captureFileName;
    }

    @Override
    protected void onImageFileSaved() {
        super.onImageFileSaved();

        WritableMap event = Arguments.createMap();
        event.putString("message", getCapturedFileName());
        event.putString("type", "camera_capture");
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event
        );
    }

    @Override
    protected void onBarcodeScanned(String barcodeResult) {
        super.onBarcodeScanned(barcodeResult);
        WritableMap event = Arguments.createMap();
        event.putString("message", barcodeResult);
        event.putString("type", "barcode_capture");
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event
        );
    }
}
