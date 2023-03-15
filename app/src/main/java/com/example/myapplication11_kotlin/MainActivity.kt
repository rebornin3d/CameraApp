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
}