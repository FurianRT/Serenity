package com.furianrt.storage.internal.database

import android.net.Uri
import androidx.room.TypeConverter

internal class DatabaseTypeConverter {

    @TypeConverter
    fun stringToUri(value: String): Uri = Uri.parse(value)

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()
}