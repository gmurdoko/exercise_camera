package com.example.exercise_camera

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.FileStore
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val SELECT_FILE_FROM_STORAGE = 33

    val OPEN_CAMERA_REQUEST_CODE = 23
    lateinit var currentPhotoPath : String
    lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun browseFile(view: View){
        val selectFileIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        val selectFileIntent = Intent(Intent.ACTION_PICK, )
        startActivityForResult(selectFileIntent, SELECT_FILE_FROM_STORAGE)
    }

    fun openCamera(view: View){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.resolveActivity(packageManager)
        photoFile = createImageFile()
        val photoURI = FileProvider.getUriForFile(this,"com.example.exercise_camera.fileprovider", photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(cameraIntent,OPEN_CAMERA_REQUEST_CODE)

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OPEN_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(imageBitmap)
        }

        if(requestCode == SELECT_FILE_FROM_STORAGE && resultCode == Activity.RESULT_OK){
//            read uri
//            val uriFile = data?.data
//            imageView.setImageURI(uriFile)
            val originalPath : String? = getOriginalPathFromUri(data?.data!!)
            //Image File
            val imageFile = File(originalPath)

            val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageView.setImageBitmap(imageBitmap)
        }

    }
    fun getOriginalPathFromUri(contentUri: Uri): String?{
        var originalPath: String? = null
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor?.moveToFirst()!!) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            originalPath = cursor.getString(columnIndex)
        }
        return originalPath
    }
}