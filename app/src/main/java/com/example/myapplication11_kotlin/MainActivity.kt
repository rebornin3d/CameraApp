package com.example.myapplication11_kotlin

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView

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
        val grayscaleBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
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
        System.out.println("width : "+ width);

        val height = bitmap.height
        System.out.println("height : "+ height);

        val pixels = IntArray(width * height)
        System.out.println("pixels : " + pixels);

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // The `pixels` array now contains the grayscale pixel values of the image

        // TODO: Convert the grayscale pixels to sound signal
        // You can use the `pixels` array to generate a sound signal based on the grayscale values

        imageView.setImageBitmap(bitmap)


        System.out.println("bitmap.getPixels : " +  bitmap.getPixels(pixels, 0, width, 0, 0, width, height));
    }
}



