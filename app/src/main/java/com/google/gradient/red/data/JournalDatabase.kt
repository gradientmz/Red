package com.google.gradient.red.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [JournalData::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class JournalDatabase : RoomDatabase() {

    abstract fun JournalDao(): JournalDao

    companion object {

        @Volatile
        private var INSTANCE: JournalDatabase? = null

        fun getDatabase(context: Context): JournalDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}