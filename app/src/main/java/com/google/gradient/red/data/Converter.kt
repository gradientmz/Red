package com.google.gradient.red.data

import androidx.room.TypeConverter
import com.google.gradient.red.data.models.Mood

class Converter {

    @TypeConverter
    fun fromMood(mood: Mood): String {
        return mood.name
    }

    @TypeConverter
    fun toMood(mood: String): Mood {
        return Mood.valueOf(mood)
    }
}