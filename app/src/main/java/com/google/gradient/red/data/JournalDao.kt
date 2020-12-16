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

    @Query("SELECT * FROM journal_table WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchDatabase(searchQuery: String) : LiveData<List<JournalData>>

    @Query("SELECT * FROM journal_table ORDER BY CASE WHEN mood LIKE 'Happy%' THEN 1 WHEN mood LIKE 'Okay%' THEN 2 WHEN mood LIKE 'Upset%' THEN 3 END")
    fun sortByGoodMood(): LiveData<List<JournalData>>

    @Query("SELECT * FROM journal_table ORDER BY CASE WHEN mood LIKE 'Upset%' THEN 1 WHEN mood LIKE 'Okay%' THEN 2 WHEN mood LIKE 'Happy%' THEN 3 END")
    fun sortByBadMood(): LiveData<List<JournalData>>
}