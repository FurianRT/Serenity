package com.furianrt.storage.internal.database

import android.net.Uri
import androidx.room.TypeConverter
import java.time.ZonedDateTime

internal class TypeConverter {

    @TypeConverter
    fun stringToUri(value: String): Uri = Uri.parse(value)

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToZonedDateTime(date: String): ZonedDateTime = ZonedDateTime.parse(date)

    @TypeConverter
    fun zonedDateTimeToString(date: ZonedDateTime): String = date.toString()
}