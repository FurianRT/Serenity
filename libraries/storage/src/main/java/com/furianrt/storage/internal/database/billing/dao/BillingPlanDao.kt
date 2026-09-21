package com.furianrt.storage.internal.database.billing.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.furianrt.storage.internal.database.billing.entities.EntryBillingPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface BillingPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<EntryBillingPlan>)

    @Query("DELETE FROM ${EntryBillingPlan.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${EntryBillingPlan.TABLE_NAME}")
    fun getBillingPlans(): Flow<List<EntryBillingPlan>>

    @Transaction
    suspend fun clearAndInsert(entries: List<EntryBillingPlan>) {
        deleteAll()
        insert(entries)
    }
}