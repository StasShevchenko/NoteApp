package com.example.noteapp.di

import android.app.Application
import androidx.room.Room
import com.example.noteapp.data.data_source.NotesDao
import com.example.noteapp.data.data_source.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(app: Application): NotesDatabase{
        return Room.databaseBuilder(
            app,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        )
            .createFromAsset("note_app_db.db")
            .build()
    }

    @Provides
    fun provideNotesDao(db: NotesDatabase): NotesDao {
        return db.notesDao
    }
}