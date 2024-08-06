package gov.orsac.hideapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import gov.orsac.hideapp.ui.image.FullImageActivity
import java.io.File

class ImageAdapter(private val context: Context, private val files: Array<File>) : BaseAdapter() {

    override fun getCount(): Int {
        return files.size
    }

    override fun getItem(position: Int): Any {
        return files[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView = if (convertView == null) {
            ImageView(context).apply {
                val layoutParams = LinearLayout.LayoutParams(300, 300).apply {
                    setMargins(10, 8, 10, 8)  // set margins (left, top, right, bottom)
                }
                this.layoutParams = layoutParams
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        } else {
            convertView as ImageView
        }

        val imgFile = files[position]
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            imageView.setImageBitmap(bitmap)
        } else {
            // Let's handle the case where the image doesn't exist, just in case
            imageView.setImageResource(android.R.drawable.ic_delete) // Placeholder image
        }

        imageView.setOnClickListener {
            val intent = Intent(context, FullImageActivity::class.java).apply {
                putExtra("imagePath", imgFile.absolutePath)
            }
            context.startActivity(intent)
        }

        return imageView
    }
}