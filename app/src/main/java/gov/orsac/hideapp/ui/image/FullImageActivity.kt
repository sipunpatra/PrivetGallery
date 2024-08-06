package gov.orsac.hideapp.ui.image

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import gov.orsac.hideapp.R
import java.io.File

//class FullImageActivity : AppCompatActivity() {
//
//    private var imagePath: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_full_image)
//
//        imagePath = intent.getStringExtra("imagePath")
//
//        val imageView: ImageView = findViewById(R.id.fullImageView)
//        val imagePath = intent.getStringExtra("imagePath")
//        imagePath?.let {
//            val imgFile = File(it)
//            if (imgFile.exists()) {
//                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//                imageView.setImageBitmap(bitmap)
//            }
//        }
//
//
//
//    }
//
//    private fun shareImage() {
//        imagePath?.let {
//            val imgFile = File(it)
//            if (imgFile.exists()) {
//                val uri: Uri = FileProvider.getUriForFile(
//                    this,
//                    "${applicationContext.packageName}.fileprovider",
//                    imgFile
//                )
//                val shareIntent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    type = "image/*"
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                }
//                startActivity(Intent.createChooser(shareIntent, "Share Image"))
//            } else {
//                Toast.makeText(this, "Image file not found!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
class FullImageActivity : AppCompatActivity() {

    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        imagePath = intent.getStringExtra("imagePath")

        val imageView: ImageView = findViewById(R.id.fullImageView)
        imagePath?.let {
            val imgFile = File(it)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            }
        }

        imageView.setOnClickListener {
            showImageOptionsDialog()
        }
    }

    private fun showImageOptionsDialog() {
        val options = arrayOf("Share", "Like")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> shareImage()
                1 -> likeImage()
            }
        }
        builder.show()
    }

    private fun shareImage() {
        imagePath?.let {
            val imgFile = File(it)
            if (imgFile.exists()) {
                val uri: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileprovider",
                    imgFile
                )
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            } else {
                Toast.makeText(this, "Image file not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun likeImage() {
        Toast.makeText(this, "Image liked!", Toast.LENGTH_SHORT).show()
    }
}
