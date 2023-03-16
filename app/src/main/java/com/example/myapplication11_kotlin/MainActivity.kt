package com.example.myapplication11_kotlin

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import java.lang.Math.PI
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val openButton: Button = findViewById(R.id.openButton)
        openButton.setOnClickListener { openGallery() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            uri?.let { grayscaleImage(it) }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            uri?.let { grayscaleImage_2(it) }
        }
    }

    private fun grayscaleImage(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        val grayscaleBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)
        imageView.setImageBitmap(grayscaleBitmap)
    }


    private fun grayscaleImage_2(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val width = bitmap.width
        System.out.println("width : " + width);

        val height = bitmap.height
        System.out.println("height : " + height);

        val pixels = IntArray(width * height)
        System.out.println("pixels : " + pixels);

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // The `pixels` array now contains the grayscale pixel values of the image

        val numChannels = 1 // Mono sound signal
        //val numSamples = duration * sampleRate * numChannels / 1000 // Number of samples in the sound signal
        val duration = 5000 // Length of the sound signal in milliseconds
        val sampleRate = 44000 // or 8000
        // Sample rate of the sound signal in Hz
        val numSamples = duration * sampleRate / 1000000 // Number of samples in the sound signal
        val sampleData = ShortArray(numSamples) // Array to store the sound samples


        // Generate the sound samples based on the grayscale values
        for (i in 0 until numSamples) {
            val index = i * pixels.size / numSamples // Map the sample index to a pixel index
            val grayscaleValue =
                pixels[index] and 0xff // Extract the grayscale value from the pixel value
            val frequency =
                grayscaleValue * 20f // Map the grayscale value to a frequency in the range of 0-440 Hz
            val amplitude = 32767f // Maximum amplitude for 16-bit signed PCM data
            sampleData[i] = (amplitude * sin(2 * PI.toFloat() * frequency * i / sampleRate)).toInt()
                .toShort() // Generate the sound sample as a sine wave
            System.out.println("// Generate the sound samples based on the grayscale values");
        }

        // Create an AudioTrack object to play the sound signal
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(numSamples * 2)
            .build()

        // Write the sound samples to the AudioTrack buffer and play the sound signal
        audioTrack.write(sampleData, 0, numSamples)
        audioTrack.play()
        System.out.println("audioTrack.play()");

        imageView.setImageBitmap(bitmap)
    }


}




