package com.google.gradient.red.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gradient.red.data.JournalDatabase
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JournalViewModel(application: Application): AndroidViewModel(application) {

    private val journalDao = JournalDatabase.getDatabase(application).JournalDao()
    private val repository: JournalRepository

    val getAllData: LiveData<List<JournalData>>

    init {
        repository = JournalRepository(journalDao)
        getAllData = repository.getAllData
    }

    fun insertData(journalData: JournalData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(journalData)
        }
    }

    fun updateData(journalData: JournalData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(journalData)
        }
    }

    fun deleteItem(journalData: JournalData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(journalData)
        }
    }

}