package com.google.gradient.red.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gradient.red.data.models.Mood
import java.io.ByteArrayOutputStream

class Converter {

    @TypeConverter
    fun fromMood(mood: Mood): String {
        return mood.name
    }

    @TypeConverter
    fun toMood(mood: String): Mood {
        return Mood.valueOf(mood)
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size!!)
    }
}