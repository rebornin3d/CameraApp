package com.example.myapplication4;

import static android.service.controls.ControlsProviderService.TAG;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;

import java.util.List;


public class CameraUtils {
    private static final String TAG = "CameraUtils";

    public static Camera mCamera;

    // Open the camera
    public boolean openCamera(int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to open camera: " + e.getMessage());
            return false;
        }
    }

    // Get the camera instance
    public Camera getCamera() {
        return mCamera;
    }

    // Release the camera
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
           // mCamera = null;
        }
    }

    // Configure the camera parameters
    public void configureCameraParameters(Camera.Parameters parameters, int previewWidth, int previewHeight) {
        // Set the preview size and format
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes, previewWidth, previewHeight);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        parameters.setPreviewFormat(ImageFormat.NV21);

        // Set the focus mode
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        // Set the white balance
        List<String> whiteBalanceModes = parameters.getSupportedWhiteBalance();
        if (whiteBalanceModes != null && whiteBalanceModes.size() > 0) {
            String whiteBalance = whiteBalanceModes.get(0);
            parameters.setWhiteBalance(whiteBalance);
        }

        // Set the exposure compensation
        int minExposureCompensation = parameters.getMinExposureCompensation();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();
        if (minExposureCompensation != 0 || maxExposureCompensation != 0) {
            parameters.setExposureCompensation(0);
        }
    }

    // Get the optimal preview size
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int previewWidth, int previewHeight) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) previewHeight / previewWidth;

        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = previewHeight;

        // Find the size that matches the target aspect ratio and has the smallest difference
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // If no size matches the target aspect ratio, choose the one with the smallest difference
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}

