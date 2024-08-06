package gov.orsac.hideapp.ui.note.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "note_table")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "note")
    var notes: String = "",

    @ColumnInfo(name = "date")
    var date: String = "",


): Serializable
