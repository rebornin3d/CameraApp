package com.example.myapplication4;

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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AudioProcessing";

    private CameraUtils mCameraUtils;
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    //private ImageReader imageReader;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_8BIT;

    private static final int SAMPLE_RATE = 11025; // or 8000
    private static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);


    // private static final int SAMPLE_RATE = 44100; // or any other sampling rate that you are using
    //private static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);


    private static final String outputFilePath = "/home/florin/AndroidStudioProjects/CameraApp/app/src/main/res/layout/output.wav"; // replace with the desired output file path


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_main);


        // Get the camera parameters
        // Camera.Parameters params = mCamera.setParameters();
        // System.out.println("params" + params);

        // Get the preview size from the camera parameters
//        Camera.Size previewSize = params.getPreviewSize();
//        System.out.println("previewSize" + previewSize);


        // Get the number of cameras on the device
        int numCameras = Camera.getNumberOfCameras();

        System.out.println("numCameras" + numCameras);

        // For each camera, get the camera info and print its parameters
        for (int i = 0; i < numCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            System.out.println("cameraInfoIs" + cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera camera = Camera.open(i);

                // Get the camera parameters
                Camera.Parameters parameters = camera.getParameters();
                System.out.println("parameters are " + parameters.flatten());
            }
        }


//                // Print the camera parameters
//                Log.d(TAG, "Camera " + i + " parameters:");
//                Log.d(TAG, "Flash modes: " + parameters.getSupportedFlashModes());
//                Log.d(TAG, "Picture sizes: " + parameters.getSupportedPictureSizes());
//                Log.d(TAG, "Preview sizes: " + parameters.getSupportedPreviewSizes());


        // Initialize the CameraUtils object
        mCameraUtils = new CameraUtils();

        // Find the SurfaceView for displaying the camera preview
        surfaceView = findViewById(R.id.preview);

        // Set up the SurfaceHolder for the SurfaceView
        mHolder = surfaceView.getHolder();

        mCamera = Camera.open();
        System.out.println("Camera.CameraInfo.CAMERA_FACING_BACK" + mCameraUtils.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK));
        if (mCamera == null) {
            if (!mCameraUtils.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                Log.e(TAG, "Failed to open camera");
                return;
            }
            Log.e(TAG, "Failed to open camera");
            return;
        }
        mCamera = mCameraUtils.getCamera();


        // Initialize the ImageReader with the desired frame size and format
        int imageWidth = 3840;
        int imageHeight = 2160;
        ImageReader imageReader = ImageReader.newInstance(imageWidth, imageHeight, ImageFormat.YUV_420_888, 1);

        // Set up a handler thread to receive the captured frames
        HandlerThread handlerThread = new HandlerThread("ImageReader");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        System.out.println("Handler handler = new Handler(handlerThread.getLooper());");
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                // Obtain the Image object from the ImageReader
                Image image = reader.acquireLatestImage();

                Log.d(TAG, "onImageAvailable called");


                // Extract the pixel data from the Image object
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer bufferY = planes[0].getBuffer();
                ByteBuffer bufferU = planes[1].getBuffer();
                ByteBuffer bufferV = planes[2].getBuffer();
                byte[] dataY = new byte[bufferY.remaining()];
                byte[] dataU = new byte[bufferU.remaining()];
                byte[] dataV = new byte[bufferV.remaining()];
                bufferY.get(dataY);
                bufferU.get(dataU);
                bufferV.get(dataV);


                // Convert the YUV pixel data to a byte array
                byte[] data = new byte[imageWidth * imageHeight];
                int i, j, k;
                for (i = 0, k = 0; i < imageHeight; i++) {
                    for (j = 0; j < imageWidth; j++, k++) {
                        int y = dataY[i * imageWidth + j] & 0xff;
                        int u = dataU[(i / 2) * (imageWidth / 2) + (j / 2)] & 0xff;
                        int v = dataV[(i / 2) * (imageWidth / 2) + (j / 2)] & 0xff;
                        int r = (int) (1.164 * (y - 16) + 1.596 * (v - 128));
                        int g = (int) (1.164 * (y - 16) - 0.392 * (u - 128) - 0.813 * (v - 128));
                        int b = (int) (1.164 * (y - 16) + 2.017 * (u - 128));
                        r = Math.min(Math.max(r, 0), 255);
                        g = Math.min(Math.max(g, 0), 255);
                        b = Math.min(Math.max(b, 0), 255);
                        int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                        data[k] = (byte) gray;
                    }
                }


                // Compute the frequency content of the pixel data using Fourier transforms
                double[] signal = new double[data.length];
                for (int m = 0; m < data.length; m++) {
                    signal[m] = data[m] / 255.0;
                }
                double[] spectrum = FourierTransform.fft(signal);
                double[] freqBins = new double[spectrum.length];
                double freqBinWidth = SAMPLE_RATE / (2 * spectrum.length);
                for (int m = 0; m < freqBins.length; m++) {
                    freqBins[m] = m * freqBinWidth;
                }


                // Map the frequency bins to audio frequencies
                double[] audioFreqs = new double[freqBins.length];
                for (int m = 0; m < audioFreqs.length; m++) {
                    audioFreqs[m] = m * freqBinWidth;
                }


                // Map the spectrum to audio amplitudes
                double[] audioAmps = new double[spectrum.length];
                for (int m = 0; m < audioAmps.length; m++) {
                    audioAmps[m] = Math.sqrt(spectrum[m] * spectrum[m]);
                }

                // Generate the audio waveform by applying an inverse Fourier transform to the spectrum
                double[] audioData = FourierTransform.ifft(spectrum);


                // Normalize the audio data and convert it to a byte array
                double maxAbsValue = 0.0;
                for (int m = 0; m < audioData.length; m++) {
                    if (Math.abs(audioData[m]) > maxAbsValue) {
                        maxAbsValue = Math.abs(audioData[m]);
                    }
                }
                byte[] audioBytes = new byte[audioData.length];
                for (int m = 0; m < audioData.length; m++) {
                    audioBytes[m] = (byte) (audioData[m] * 127 * 2 / maxAbsValue);
                }

                System.out.println("audioBytes" + audioBytes);


                // Create the AudioTrack object
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE, AudioTrack.MODE_STREAM);


                // Write the audio data to the AudioTrack object in chunks
                for (int z = 0; z < audioBytes.length; z += BUFFER_SIZE) {
                    int bytesWritten = audioTrack.write(audioBytes, z, Math.min(BUFFER_SIZE, audioBytes.length - z));
                    Log.d(TAG, "Bytes written: " + bytesWritten);
                    Log.d(TAG, "Playback head position: " + audioTrack.getPlaybackHeadPosition());
                }

                // Start playing the audio data
                audioTrack.play();

                // Stop playing the audio data
                audioTrack.stop();
                audioTrack.release();


                // Release the Image object
                image.close();
            }
        }, handler);

        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        // Log.d(TAG, "Preview frame received");
                    }
                });


                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mCamera.startPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mCamera.stopPreview();
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mCamera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
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
                System.out.println("Camera.CameraInfo.CAMERA_FACING_BACK");
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
