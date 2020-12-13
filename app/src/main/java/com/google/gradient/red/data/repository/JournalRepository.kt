package com.google.gradient.red.data.repository

import androidx.lifecycle.LiveData
import com.google.gradient.red.data.JournalDao
import com.google.gradient.red.data.models.JournalData

class JournalRepository(private val journalDao: JournalDao) {

    val getAllData: LiveData<List<JournalData>> = journalDao.getAllData()

    suspend fun insertData(journalData: JournalData) {
        journalDao.insertData(journalData)
    }

    suspend fun updateData(journalData: JournalData) {
        journalDao.updateData(journalData)
    }

    suspend fun deleteItem(journalData: JournalData) {
        journalDao.deleteItem(journalData)
    }
}