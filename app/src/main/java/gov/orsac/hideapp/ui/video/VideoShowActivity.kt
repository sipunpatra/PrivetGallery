package gov.orsac.hideapp.ui.video

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import gov.orsac.hideapp.R

class VideoShowActivity : AppCompatActivity() {
    private lateinit var playerView: StyledPlayerView
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_show)

        playerView = findViewById(R.id.player_view)

        val videoUriString = intent.getStringExtra("video_uri")
        val videoUri = videoUriString?.let { Uri.parse(it) }

        if (videoUri != null) {
            initializePlayer(videoUri)
        } else {
            Toast.makeText(this, "No video URI found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializePlayer(uri: Uri) {
        player = ExoPlayer.Builder(this).build().also {
            playerView.player = it
            val mediaItem = MediaItem.fromUri(uri)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}