package gov.orsac.hideapp.ui.note.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import gov.orsac.hideapp.databinding.ActivityNoteBinding
import gov.orsac.hideapp.ui.note.adapter.NoteAdapter
import gov.orsac.hideapp.ui.note.db.NoteDao
import gov.orsac.hideapp.ui.note.db.NoteDataBase
import gov.orsac.hideapp.ui.note.db.Notes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteActivity : AppCompatActivity(),NoteAdapter.NotesClickListener {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var noteDao: NoteDao

    //    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var database: NoteDataBase
    private var notes: MutableList<Notes> = ArrayList()
    private var selectedNotes: Notes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = NoteDataBase.getInstance(this)
        noteDao = database.noteDao()


        // in this we are specifying drag direction and position to right and left
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                val deletedNote: Notes =
                    notes[viewHolder.adapterPosition]

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                notes.removeAt(viewHolder.adapterPosition)

                // below line is to notify our item is removed from adapter.
                noteAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                // below line is to display our snackbar with action.
                // below line is to display our snackbar with action.
                // below line is to display our snackbar with action.
                // Delete from database in a coroutine
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        noteDao.delete(deletedNote)
                    }
                    Snackbar.make(binding.recyclerView, "Deleted " + deletedNote.title, Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            notes.add(position, deletedNote)
                            noteAdapter.notifyItemInserted(position)

                            // Re-insert into database in a coroutine
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    noteDao.insert(deletedNote)
                                }

                            }
                        }.show()
                }
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(binding.recyclerView)


        // Load notes from the database in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            notes = database.noteDao().getAll().toMutableList()
            // Update the RecyclerView on the main thread
            launch(Dispatchers.Main) {
                updateRecycle(notes)
            }
        }
        binding.btnAddNote.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivityForResult(intent, 101)
        }

}

    private fun updateRecycle(notes: MutableList<Notes>) {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        noteAdapter= NoteAdapter(this@NoteActivity, notes, this)
        binding.recyclerView.adapter = noteAdapter
    }

    override fun onClick(notes: Notes?) {
        val intent = Intent(this@NoteActivity, EditNoteActivity::class.java)
        intent.putExtra("old_notes", notes)
        startActivityForResult(intent, 102)
    }

    override fun onLongPress(notes: Notes?, cardView: CardView?) {
        Log.d("TAG", "onLongPress: Delete")
            Toast.makeText(applicationContext, "Are you Delete this note ", Toast.LENGTH_SHORT).show()
    }



    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val newNotes = data?.getSerializableExtra("note") as? Notes ?: return
            when (requestCode) {
                101 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        database.noteDao().insert(newNotes)
                        notes.clear()
                        notes.addAll(database.noteDao().getAll())
                        withContext(Dispatchers.Main) {
                            noteAdapter.notifyDataSetChanged()
                        }
                    }
                }
                102 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        database.noteDao().update(newNotes.id, newNotes.title, newNotes.notes)
                        notes.clear()
                        notes.addAll(database.noteDao().getAll())
                        withContext(Dispatchers.Main) {
                            noteAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}