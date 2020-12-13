package com.google.gradient.red.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gradient.red.data.models.JournalData

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_table ORDER BY id DESC")
    fun getAllData(): LiveData<List<JournalData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(journalData: JournalData)

    @Update
    suspend fun updateData(journalData: JournalData)

    @Delete
    suspend fun deleteItem(journalData: JournalData)

    @Query("DELETE FROM journal_table")
    suspend fun deleteAll()
}