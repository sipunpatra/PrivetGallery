package gov.orsac.hideapp.ui.note.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import gov.orsac.hideapp.R
import gov.orsac.hideapp.databinding.ActivityEditNoteBinding
import gov.orsac.hideapp.databinding.ActivityNoteBinding
import gov.orsac.hideapp.ui.note.db.Notes
import java.text.SimpleDateFormat
import java.util.Date

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding

    private var notes: Notes? = null
    private var isOldNotes = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inti()
    }
    private fun inti() {
        notes = intent.getSerializableExtra("old_notes") as? Notes
        notes?.let {
            binding.editTextNoteTitle.setText(it.title)
            binding.editTextNote.setText(it.notes)
            isOldNotes = true
        }

        binding.back.setOnClickListener {
           onBackPressed()
        }
        binding.check.setOnClickListener {
            val title = binding.editTextNoteTitle.text.toString()
            val description = binding.editTextNote.text.toString()

            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter the description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
            val date = Date()
            if (!isOldNotes) {
                notes = Notes()
            }

            notes?.apply {
                this.title = title
                this.notes = description
                this.date = format.format(date)
            }

            val intent = Intent().apply {
                putExtra("note", notes)
            }
            setResult(Activity.RESULT_OK, intent)

            finish()
        }
    }
}