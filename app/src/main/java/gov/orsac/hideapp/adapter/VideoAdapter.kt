package gov.orsac.hideapp.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gov.orsac.hideapp.R

class VideoAdapter(
    private val context: Context,
    private val videoList: List<Uri>,
    private val onVideoClick: (Uri) -> Unit

) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.video_thumbnail)
        val videoDuration :TextView =itemView.findViewById(R.id.video_duration)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onVideoClick(videoList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val uri = videoList[position]

        // Thumbnail
        Glide.with(context)
            .load(uri)
            .thumbnail(0.1f)
            .centerCrop()
            .into(holder.thumbnail)

        // Duration
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            holder.videoDuration.text = formatDuration(durationMs)
            retriever.release()
        } catch (e: Exception) {
            holder.videoDuration.text = "--:--"
        }
    }

    private fun formatDuration(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }


    override fun getItemCount(): Int = videoList.size
}
