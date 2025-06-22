package gov.orsac.hideapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import gov.orsac.hideapp.R
import gov.orsac.hideapp.ui.image.ViewHiddenPhotosActivity
import gov.orsac.hideapp.ui.note.ui.NoteActivity
import gov.orsac.hideapp.ui.video.VideoActivity

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 100
    private val PICK_IMAGE_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image :CardView = findViewById(R.id.cv_image)
        val note :CardView = findViewById(R.id.cv_note)
        val browser :CardView = findViewById(R.id.cv_browser)
        val video :CardView = findViewById(R.id.cv_video)

        image.setOnClickListener {
            val intent = Intent(this, ViewHiddenPhotosActivity::class.java)
            startActivity(intent)
        }
        note.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
//            Toast.makeText(applicationContext, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
        browser.setOnClickListener {
            openIncognitoTab()

        }
        video.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
//            Toast.makeText(applicationContext, "Coming Soon", Toast.LENGTH_SHORT).show()

        }

    }

    private fun openIncognitoTab() {
        val incognitoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
        incognitoIntent.setPackage("com.android.chrome")
        incognitoIntent.putExtra("com.android.browser.application_id", "com.android.chrome")
        incognitoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        incognitoIntent.putExtra("com.android.browser.application_id", "com.android.chrome")
        incognitoIntent.putExtra("com.android.browser.headers", arrayOf(
            "X-Requested-With:com.android.browser",
            "X-Content-Type-Options:nosniff"
        ))
        incognitoIntent.putExtra("com.android.chrome.incognito", true)

        try {
            startActivity(incognitoIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Chrome not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val result =ContextCompat.checkSelfPermission(
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

}