package com.google.gradient.red.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_table")
data class JournalData (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var mood: Mood,
    var description: String
)