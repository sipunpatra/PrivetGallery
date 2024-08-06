package gov.orsac.hideapp.ui.note.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 2, exportSchema = false)
abstract class NoteDataBase :RoomDatabase(){
        abstract fun noteDao(): NoteDao

        companion object {
            @Volatile
            private var INSTANCE: NoteDataBase? = null
            private const val DATABASE_NAME = "NoteApp"
            fun getInstance(context: Context): NoteDataBase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDataBase::class.java,
                        DATABASE_NAME).build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
