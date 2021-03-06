package com.google.gradient.red.data.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "journal_table")
@Parcelize
data class JournalData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var mood: Mood,
    var description: String,
    var date: String,
    var image: Bitmap?
): Parcelable