package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteCustomBackground
import com.furianrt.storage.internal.database.notes.entities.PartNoteCustomBackgroundId
import com.furianrt.storage.internal.database.notes.entities.PartNoteCustomBackgroundUri
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CustomBackgroundDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(background: EntryNoteCustomBackground)

    @Update(entity = EntryNoteCustomBackground::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartNoteCustomBackgroundUri)

    @Delete(entity = EntryNoteCustomBackground::class)
    suspend fun delete(data: PartNoteCustomBackgroundId)

    @Query(
        """
    SELECT *
    FROM ${EntryNoteCustomBackground.TABLE_NAME}
    WHERE ${EntryNoteCustomBackground.FIELD_IS_SAVED} = 0
    """
    )
    fun getUnsavedBackgrounds(): Flow<List<EntryNoteCustomBackground>>

    @Query("SELECT * FROM ${EntryNoteCustomBackground.TABLE_NAME}")
    fun getAllBackgrounds(): Flow<List<EntryNoteCustomBackground>>

    @Query(
        """
    SELECT EXISTS(
        SELECT *
        FROM ${EntryNoteCustomBackground.TABLE_NAME}
        WHERE ${EntryNoteCustomBackground.FIELD_ID} = :backgroundId
        AND ${EntryNoteCustomBackground.FIELD_IS_SAVED} = 1
    )
    """
    )
    suspend fun isSaved(backgroundId: String): Boolean
}