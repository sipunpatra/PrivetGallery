package gov.orsac.hideapp.ui.image

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.GridView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gov.orsac.hideapp.R
import gov.orsac.hideapp.adapter.ImageAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ViewHiddenPhotosActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 100
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var gridView: GridView
    private lateinit var hiddenDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_hidden_photos)
        val fab = findViewById<FloatingActionButton>(R.id.btn_Add_photo)

         gridView= findViewById(R.id.gridView)
         hiddenDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), ".hidden")

        fab.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.TIRAMISU){
                if (checkPermission()){
                    selectPhoto()
                } else {
                    requestPermission()
                }
            }else{
                selectPhoto()
            }
        }
      updateGridView()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPhoto()
            }
        }
    }
    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    saveImageToHiddenDirectory(imageUri)
                }
            } else if (data.data != null) {
                val imageUri: Uri? = data.data
                imageUri?.let { uri ->
                    saveImageToHiddenDirectory(uri)
                }
            }
            updateGridView()
        }
    }
    private fun saveImageToHiddenDirectory(imageUri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val hiddenDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), ".hidden")
        if (!hiddenDir.exists()) hiddenDir.mkdirs()
        val hiddenFile = File(hiddenDir, System.currentTimeMillis().toString() + ".jpg")
        val outputStream = FileOutputStream(hiddenFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        // Delete the original image from the gallery
        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val imageId = getContentIdFromUri(imageUri)
            if (imageId != null) {
                val uriToDelete = ContentUris.withAppendedId(contentUri, imageId)
                contentResolver.delete(uriToDelete, null, null)
            } else {
                // Attempt to delete using DocumentsContract if content ID is not found
                contentResolver.delete(imageUri, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getContentIdFromUri(uri: Uri): Long? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                return it.getLong(idIndex)
            }
        }
        return null
    }
    private fun updateGridView() {
        if (hiddenDir.exists()) {
            val hiddenFiles = hiddenDir.listFiles()
            if (hiddenFiles != null) {
                val sortedFiles = hiddenFiles.sortedBy { it.lastModified() }
                val adapter = ImageAdapter(this, sortedFiles.toTypedArray())
                gridView.adapter = adapter
            }
        }
    }
}