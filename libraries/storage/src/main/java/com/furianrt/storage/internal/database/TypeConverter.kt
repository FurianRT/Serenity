package com.furianrt.storage.internal.database

import android.net.Uri
import androidx.room.TypeConverter
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import kotlinx.serialization.encodeToString

internal class TypeConverter {

    @TypeConverter
    fun stringToUri(value: String): Uri = Uri.parse(value)

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToZonedDateTime(value: String): ZonedDateTime = ZonedDateTime.parse(value)

    @TypeConverter
    fun zonedDateTimeToString(date: ZonedDateTime): String = date.toString()

    @TypeConverter
    fun stringToNoteFont(value: String): NoteFontFamily = NoteFontFamily.fromString(value)

    @TypeConverter
    fun noteFontToString(font: NoteFontFamily): String = font.name

    @TypeConverter
    fun stringToNoteFontColor(value: String): NoteFontColor = NoteFontColor.fromString(value)

    @TypeConverter
    fun noteFontColorToString(fontColor: NoteFontColor): String = fontColor.name

    @TypeConverter
    fun stringToListOfFloat(value: String): List<Float> = Json.decodeFromString(value)

    @TypeConverter
    fun listOfFloatToString(list: List<Float>): String = Json.encodeToString(list)
}