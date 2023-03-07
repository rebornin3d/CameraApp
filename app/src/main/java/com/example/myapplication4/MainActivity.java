package com.example.myapplication4;

import android.hardware.Camera;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication4.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CameraUtils mCameraUtils;
    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        // Initialize the CameraUtils object
        mCameraUtils = new CameraUtils();

        // Find the SurfaceView for displaying the camera preview
        mPreview = findViewById(R.id.preview);

        // Set up the SurfaceHolder for the SurfaceView
        mHolder = mPreview.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Do nothing
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera != null) {
                    // Configure the camera parameters
                    Camera.Parameters parameters = mCamera.getParameters();
                    Log.e(TAG, "mCamera.getParameters()" + mCamera.getParameters());
                    mCameraUtils.configureCameraParameters(parameters, width, height);

                    mCamera.setDisplayOrientation(180);


                    mCamera.setParameters(parameters);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume()");
        super.onResume();

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
