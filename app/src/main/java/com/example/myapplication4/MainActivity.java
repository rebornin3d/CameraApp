package com.example.myapplication4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CameraUtils mCameraUtils;
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder_1;


    // Initialize variables
    int frameCount = 0;
    Bitmap[] frames = new Bitmap[10];
    int[] redSum = new int[256];
    int[] greenSum = new int[256];
    int[] blueSum = new int[256];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_main);


        //Get Camera Ids
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {


            String[] cameraIds = manager.getCameraIdList();


            for (String element : cameraIds) {

                System.out.println("cameraIds are : " + element);
            }


        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }


        // Initialize the CameraUtils object
        mCameraUtils = new CameraUtils();

        // Find the SurfaceView for displaying the camera preview
        surfaceView = findViewById(R.id.preview);

        // Set up the SurfaceHolder for the SurfaceView
        mHolder_1 = surfaceView.getHolder();

        // Lock the Canvas object to acquire the video frames
        Canvas canvas = mHolder_1.lockCanvas();
        Log.e(TAG, "mHolder_1.getSurface()" + mHolder_1.getSurface());

        // Create a new Bitmap object with the same dimensions as the video frames
          Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

       //  Get the dimensions of the video frames
        int width = canvas.getWidth();
        int height = canvas.getHeight();


        holder.unlockCanvasAndPost(canvas);


        mHolder_1.addCallback(new SurfaceHolder.Callback() {
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
            mCamera.setPreviewDisplay(mHolder_1);
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