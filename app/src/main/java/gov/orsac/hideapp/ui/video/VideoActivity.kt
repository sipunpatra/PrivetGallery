package gov.orsac.hideapp.ui.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gov.orsac.hideapp.R
import gov.orsac.hideapp.adapter.ImageAdapter
import gov.orsac.hideapp.adapter.VideoAdapter
import gov.orsac.hideapp.ui.note.db.Notes
import gov.orsac.hideapp.ui.video.model.Video
import java.io.File

class VideoActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 100
    private val PICK_VIDEO_REQUEST = 1

    private lateinit var videoAdapter: VideoAdapter
    private var videoUris: MutableList<Uri> = ArrayList()
    private lateinit var hiddenDir: File
    private val PREFS_NAME = "video_prefs"
    private val VIDEO_URIS_KEY = "video_uris"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val videoUrisString = prefs.getString(VIDEO_URIS_KEY, "")
        if (videoUrisString != null) {
            if (videoUrisString.isNotEmpty()) {
                videoUris = videoUrisString.split(",").map { Uri.parse(it) }.toMutableList()
                videoAdapter.notifyDataSetChanged()
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.btn_Add_video)
        hiddenDir = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), ".hidden")

        videoAdapter = VideoAdapter(applicationContext,videoUris)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)
        recyclerView.adapter = videoAdapter
        if (savedInstanceState != null) {
            // Restore saved state
            val uris = savedInstanceState.getParcelableArrayList<Uri>("videoUris")
            videoUris.addAll(uris ?: emptyList())
            videoAdapter.notifyDataSetChanged()
        }

        fab.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkPermission()) {
                    selectMedia()
                } else {
                    requestPermission()
                }
            } else {
                selectMedia()
            }
        }
    }
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED

    }
    private fun requestPermission() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
    }
    private fun selectMedia() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with media selection
                selectMedia()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK) {
//            data?.clipData?.let { clipData ->
//                for (i in 0 until clipData.itemCount) {
//                    val videoUri = clipData.getItemAt(i).uri
//                    videoUris.add(videoUri)
//                }
//            } ?: run {
//                data?.data?.let {
//                    videoUris.add(it)
//                }
//            }
//            videoAdapter.notifyDataSetChanged()
//        }
//    }
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK) {
        data?.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                val videoUri = clipData.getItemAt(i).uri
                videoUris.add(videoUri)
            }
        } ?: run {
            data?.data?.let {
                videoUris.add(it)
            }
        }
        videoAdapter.notifyDataSetChanged()
    }
}

    @SuppressLint("NotifyDataSetChanged")
    private fun handleVideoUri(videoUri: Uri) {
        videoUris.addAll(listOf(videoUri))
        videoAdapter.notifyDataSetChanged()
    }
    override fun onDestroy() {
        super.onDestroy()

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(VIDEO_URIS_KEY, videoUris.joinToString(","))
        editor.apply()
    }


}