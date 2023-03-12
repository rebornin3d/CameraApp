package com.example.myapplication4;

import java.io.FileOutputStream;
import java.io.IOException;

public class WaveFileWriter {
    public static void writeWaveFile(String filePath, byte[] data, int sampleRate, int numChannels) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            // Write the WAV file header
            outputStream.write(new byte[] { 'R', 'I', 'F', 'F' }); // RIFF header
            outputStream.write(intToByteArray(data.length + 36), 0, 4); // file size
            outputStream.write(new byte[] { 'W', 'A', 'V', 'E' }); // WAV header
            outputStream.write(new byte[] { 'f', 'm', 't', ' ' }); // fmt header
            outputStream.write(intToByteArray(16), 0, 4); // fmt chunk size
            outputStream.write(shortToByteArray((short) 1), 0, 2); // audio format (PCM)
            outputStream.write(shortToByteArray((short) numChannels), 0, 2); // number of channels
            outputStream.write(intToByteArray(sampleRate), 0, 4); // sample rate
            outputStream.write(intToByteArray(sampleRate * numChannels * 1), 0, 4); // byte rate
            outputStream.write(shortToByteArray((short) (numChannels * 1)), 0, 2); // block align
            outputStream.write(shortToByteArray((short) 8), 0, 2); // bits per sample
            outputStream.write(new byte[] { 'd', 'a', 't', 'a' }); // data header
            outputStream.write(intToByteArray(data.length), 0, 4); // data size

            // Write the audio data
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte) (value & 0xff);
        byteArray[1] = (byte) ((value >> 8) & 0xff);
        byteArray[2] = (byte) ((value >> 16) & 0xff);
        byteArray[3] = (byte) ((value >> 24) & 0xff);
        return byteArray;
    }

    private static byte[] shortToByteArray(short value) {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) (value & 0xff);
        byteArray[1] = (byte) ((value >> 8) & 0xff);
        return byteArray;
    }
}
