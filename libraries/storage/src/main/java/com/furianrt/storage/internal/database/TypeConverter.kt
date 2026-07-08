package com.furianrt.storage.internal.database

import android.net.Uri
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteTextSpan
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import androidx.core.net.toUri
import androidx.room3.ColumnTypeConverter
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import java.time.DayOfWeek
import java.time.LocalTime

internal class TypeConverter {

    @ColumnTypeConverter
    fun stringToUri(value: String): Uri = value.toUri()

    @ColumnTypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @ColumnTypeConverter
    fun stringToZonedDateTime(value: String): ZonedDateTime = ZonedDateTime.parse(value)

    @ColumnTypeConverter
    fun zonedDateTimeToString(date: ZonedDateTime): String = date.toString()

    @ColumnTypeConverter
    fun stringToNoteFont(value: String?): NoteFontFamily? = NoteFontFamily.fromString(value)

    @ColumnTypeConverter
    fun noteFontToString(font: NoteFontFamily?): String? = font?.name

    @ColumnTypeConverter
    fun stringToNoteFontColor(value: String?): NoteFontColor? = NoteFontColor.fromString(value)

    @ColumnTypeConverter
    fun noteFontColorToString(fontColor: NoteFontColor?): String? = fontColor?.name

    @ColumnTypeConverter
    fun stringToListOfFloat(value: String): List<Float> = Json.decodeFromString(value)

    @ColumnTypeConverter
    fun listOfFloatToString(list: List<Float>): String = Json.encodeToString(list)

    @ColumnTypeConverter
    fun stringToNoteTextSpans(value: String): List<NoteTextSpan> = Json.decodeFromString(value)

    @ColumnTypeConverter
    fun noteTextSpansToString(spans: List<NoteTextSpan>): String = Json.encodeToString(spans)

    @ColumnTypeConverter
    fun textAlignmentToInt(alignment: EntryNote.TextAlignment?): Int? = alignment?.value

    @ColumnTypeConverter
    fun intToTextAlignment(
        value: Int?,
    ): EntryNote.TextAlignment? = EntryNote.TextAlignment.fromValue(value)

    @ColumnTypeConverter
    fun daysOfWeekToString(days: Set<DayOfWeek>): String = days.joinToString(",") { it.name }

    @ColumnTypeConverter
    fun stringToDayOfWeek(value: String): Set<DayOfWeek> = if (value.isBlank()) {
        emptySet()
    } else {
        value.split(",").map { DayOfWeek.valueOf(it) }.toSet()
    }

    @ColumnTypeConverter
    fun localTimeToString(time: LocalTime): String = time.toString()

    @ColumnTypeConverter
    fun stringToLocalTime(value: String): LocalTime = LocalTime.parse(value)
}