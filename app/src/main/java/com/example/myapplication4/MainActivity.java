package com.example.myapplication4;

import android.hardware.Camera;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CameraUtils mCameraUtils;
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;

//    int rotation = getWindowManager().getDefaultDisplay().getRotation();
//
//    int degrees = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        // Initialize the CameraUtils object
        mCameraUtils = new CameraUtils();

        // Find the SurfaceView for displaying the camera preview
        surfaceView = findViewById(R.id.preview);

        // Set up the SurfaceHolder for the SurfaceView
        mHolder = surfaceView.getHolder();


        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Configure the camera parameters
                Camera.Parameters parameters = mCamera.getParameters();
                mCameraUtils.configureCameraParameters(parameters, width, height);


                mCamera.stopPreview();

//

                mCamera.setDisplayOrientation(90);
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mCamera.startPreview();


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");

        // Open the camera
        if (mCamera == null) {
            if (!mCameraUtils.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                Log.e(TAG, "Failed to open camera");
                return;
            }
            mCamera = mCameraUtils.getCamera();
        }

        // Start the camera preview
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Failed to start camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");

        // Release the camera
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
