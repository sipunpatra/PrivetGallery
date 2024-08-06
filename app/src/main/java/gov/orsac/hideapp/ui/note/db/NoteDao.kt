package gov.orsac.hideapp.ui.note.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Notes)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAll(): List<Notes>

    @Query("UPDATE note_table SET title = :title, note = :notes WHERE id = :id")
    fun update(id: Int, title: String, notes: String)

    @Delete
    fun delete(notes: Notes)
}