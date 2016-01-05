package com.baebae.reactnativecamera.cameralib.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.baebae.reactnativecamera.cameralib.barcode.Scan;
import com.baebae.reactnativecamera.cameralib.helpers.CameraHandlerThread;
import com.baebae.reactnativecamera.cameralib.helpers.CameraUtils;
import com.google.zxing.Result;

import java.io.File;
import java.io.FileOutputStream;

public class CameraPreviewLayout extends FrameLayout implements Camera.PreviewCallback  {
    private Camera mCamera;
    private CameraView mPreview;
    private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    private Scan barcodeScanner = null;
    private RelativeLayout cameraLayout = null;
    protected String captureFileName = "";
    public CameraPreviewLayout(Context context) {
        super(context);
        barcodeScanner = new Scan(getContext());
    }

    public final void setupLayout(Camera camera) {
        //removeAllViews();

        mPreview = new CameraView(getContext(), camera, this);
        if (cameraLayout != null) {
            removeView(cameraLayout);
        }
        cameraLayout = new RelativeLayout(getContext());
        cameraLayout.setGravity(Gravity.CENTER);
        cameraLayout.setBackgroundColor(Color.BLACK);
        cameraLayout.addView(mPreview);
        addView(cameraLayout);
        moveToBack(cameraLayout);

    }
    private void moveToBack(View currentView) {
        ViewGroup viewGroup = ((ViewGroup) currentView.getParent());
        int index = viewGroup.indexOfChild(currentView);
        for (int i = 0; i < index; i++) {
            viewGroup.bringChildToFront(viewGroup.getChildAt(i));
        }
    }
    public void startCamera(int cameraId) {
        if(mCameraHandlerThread == null) {
            mCameraHandlerThread = new CameraHandlerThread(this);
        }
        mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(Camera camera) {
        mCamera = camera;
        if(mCamera != null) {
            setupLayout(mCamera);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mFlashState != null) {
                        setFlash(mFlashState);
                    }
                }
            }, 1000);
            setAutoFocus(mAutofocusState);
        }
    }

    public void startCamera() {
        startCamera(-1);
    }

    public void stopCamera() {
        if(mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCamera.release();
            mCamera = null;
        }
        if(mCameraHandlerThread != null) {
            mCameraHandlerThread.quit();
            mCameraHandlerThread = null;
        }
    }

    public void stopCameraPreview() {
        if(mPreview != null) {
            mPreview.stopCameraPreview();
        }
    }

    protected void resumeCameraPreview() {
        if(mPreview != null) {
            mPreview.showCameraPreview();
        }
    }

    public void setFlash(boolean flag) {
        mFlashState = flag;
        toggleTorch(flag);
    }

    public boolean getFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void toggleTorch(boolean flagTorch) {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(!flagTorch) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        mAutofocusState = state;
        if(mPreview != null) {
            mPreview.setAutoFocus(state);
        }
    }

    protected void onImageFileSaved() {
    }

    protected void onBarcodeScanned(Result str) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        barcodeScanner.scanImage(data, width, height, new Scan.ResultCallback() {
            @Override
            public void onDecodeBarcode(Result result) {
                onBarcodeScanned(result);
            }
        });
    }

    Camera.PictureCallback callbackRAW = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }};
    PictureCallback callbackImage = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
            try {

                File f = new File(captureFileName);
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(arg0);
                fos.flush();
                fos.close();
                onImageFileSaved();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }};

    public void takePicture() {
        if (captureFileName.length() > 0) {
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, callbackRAW, callbackImage);
        }
    }
}
