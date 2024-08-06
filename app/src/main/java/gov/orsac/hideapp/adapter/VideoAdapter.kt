package gov.orsac.hideapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gov.orsac.hideapp.R
import gov.orsac.hideapp.ui.video.model.Video

class VideoAdapter(private val context: Context, private val videoUris: List<Uri>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)    }

    override fun getItemCount(): Int {
     return  videoUris.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoUri = videoUris[position]
        Glide.with(context).load(videoUri).into(holder.videoThumbnail)    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoThumbnail: ImageView = itemView.findViewById(R.id.video_thumbnail)
    }
}