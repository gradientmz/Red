package com.google.gradient.red.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<JournalData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(journalData: JournalData)
}