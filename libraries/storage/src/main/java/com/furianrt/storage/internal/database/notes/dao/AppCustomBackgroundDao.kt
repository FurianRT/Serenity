package com.furianrt.storage.internal.database.notes.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryAppCustomBackground
import com.furianrt.storage.internal.database.notes.entities.PartAppCustomBackgroundId
import com.furianrt.storage.internal.database.notes.entities.PartAppCustomBackgroundUri
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AppCustomBackgroundDao {

    @Upsert
    suspend fun upsert(background: EntryAppCustomBackground)

    @Update(entity = EntryAppCustomBackground::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartAppCustomBackgroundUri)

    @Delete(entity = EntryAppCustomBackground::class)
    suspend fun delete(data: PartAppCustomBackgroundId)

    @Query(
        """
    SELECT *
    FROM ${EntryAppCustomBackground.TABLE_NAME}
    WHERE ${EntryAppCustomBackground.FIELD_IS_SAVED} = 0
    """
    )
    fun getUnsavedBackgrounds(): Flow<List<EntryAppCustomBackground>>

    @Query(
        """
    SELECT *
    FROM ${EntryAppCustomBackground.TABLE_NAME}
    """
    )
    fun getAllBackgrounds(): Flow<List<EntryAppCustomBackground>>

    @Query(
        """
    SELECT *
    FROM ${EntryAppCustomBackground.TABLE_NAME}
    WHERE ${EntryAppCustomBackground.FIELD_ID} = :backgroundId
    """
    )
    fun getBackground(backgroundId: String): Flow<EntryAppCustomBackground?>

    @Query(
        """
    SELECT EXISTS(
        SELECT *
        FROM ${EntryAppCustomBackground.TABLE_NAME}
        WHERE ${EntryAppCustomBackground.FIELD_ID} = :backgroundId
        AND ${EntryAppCustomBackground.FIELD_IS_SAVED} = 1
    )
    """
    )
    suspend fun isSaved(backgroundId: String): Boolean
}
