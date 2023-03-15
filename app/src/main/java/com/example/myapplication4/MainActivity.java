package com.example.myapplication4;

import static com.example.myapplication4.CameraUtils.mCamera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.example.myapplication4.ImageProcessing;
import com.example.myapplication4.CameraUtils;


public class MainActivity extends AppCompatActivity {
//    private static final String TAG = "AudioProcessing";
//
//    private static final String AudioProcessingString = "AudioProcessing";
  //  private ImageReader mImageReader; // Declare a member variable to store the ImageReader object


  //  private static final String outputFilePath = "/home/florin/AndroidStudioProjects/CameraApp/app/src/main/res/layout/output.wav"; // replace with the desired output file path


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_main);


        ImageProcessing imgPro_1 = new ImageProcessing();

        imgPro_1.cameraDetection();

        imgPro_1.processImage_1();

        imgPro_1.processImage_2();

    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageProcessing imgPro_2 = new ImageProcessing();
        imgPro_2.onResumeSim();

    }


    @Override
    protected void onPause() {
        super.onPause();
        ImageProcessing imgPro_3 = new ImageProcessing();
        imgPro_3.onPauseSim();
    }
}
