package com.example.myapplication4;

public class FourierTransform {
    public static double[] fft(double[] x) {
        int n = x.length;
        if (n == 1) {
            return x;
        }
        double[] even = new double[n/2];
        double[] odd = new double[n/2];
        for (int i = 0; i < n/2; i++) {
            even[i] = x[2*i];
            odd[i] = x[2*i+1];
        }
        double[] spectrumEven = fft(even);
        double[] spectrumOdd = fft(odd);
        double[] spectrum = new double[n];
        for (int i = 0; i < n/2; i++) {
            double w = -2 * i * Math.PI / n;
            double twiddleReal = Math.cos(w);
            double twiddleImag = Math.sin(w);
            spectrum[i] = spectrumEven[i] + twiddleReal * spectrumOdd[i] - twiddleImag * spectrumOdd[i+n/2];
            spectrum[i+n/2] = spectrumEven[i] - twiddleReal * spectrumOdd[i] + twiddleImag * spectrumOdd[i+n/2];
        }
        return spectrum;
    }

    public static double[] ifft(double[] x) {
        int n = x.length;
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = x[i];
        }
        y = fft(y);
        for (int i = 0; i < n; i++) {
            y[i] /= n;
        }
        return y;
    }
}

