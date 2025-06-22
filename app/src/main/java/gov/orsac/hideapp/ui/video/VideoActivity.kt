package gov.orsac.hideapp.ui.video

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gov.orsac.hideapp.R
import gov.orsac.hideapp.adapter.VideoAdapter

class VideoActivity : AppCompatActivity() {

    private val PICK_VIDEO_REQUEST = 1
    private val PREFS_NAME = "video_prefs"
    private val VIDEO_URIS_KEY = "video_uris"

    private lateinit var recyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private val videoUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        recyclerView = findViewById(R.id.recyclerView)
        val fabAddVideo: FloatingActionButton = findViewById(R.id.btn_Add_video)

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.contains(VIDEO_URIS_KEY) && prefs.all[VIDEO_URIS_KEY] !is Set<*>) {
            prefs.edit().remove(VIDEO_URIS_KEY).apply()
        }

        loadVideos()

        fabAddVideo.setOnClickListener {
            if (checkStoragePermission()) {
                openVideoPicker()
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                        1001
                    )
                    false
                } else true
            }

            else -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1001
                    )
                    false
                } else true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openVideoPicker()
            } else {
                Toast.makeText(this, "Permission denied to read videos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
        }
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    private fun loadVideos() {
        videoUris.addAll(loadSavedVideos())
        videoAdapter = VideoAdapter(this, videoUris) { uri ->
            playVideo(uri)
        }
        recyclerView.adapter = videoAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedVideoUri = data.data
            selectedVideoUri?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                videoUris.add(0, uri)
                saveVideos()
                videoAdapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun loadSavedVideos(): List<Uri> {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val uriStrings = prefs.getStringSet(VIDEO_URIS_KEY, emptySet()) ?: emptySet()
        return uriStrings.map { Uri.parse(it) }
    }

    private fun saveVideos() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        val uriStrings = videoUris.map { it.toString() }.toSet()
        prefs.putStringSet(VIDEO_URIS_KEY, uriStrings)
        prefs.apply()
    }

    private fun playVideo(uri: Uri) {
        val intent = Intent(this, VideoShowActivity::class.java)
        intent.putExtra("video_uri", uri.toString())
        startActivity(intent)
    }
}
