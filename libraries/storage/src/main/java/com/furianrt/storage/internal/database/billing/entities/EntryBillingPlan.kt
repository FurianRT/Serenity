package com.furianrt.storage.internal.database.billing.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = EntryBillingPlan.TABLE_NAME)
class EntryBillingPlan(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_PRICE)
    val price: String,

    @ColumnInfo(name = FIELD_ID_TRIAL_AVAILABLE)
    val isTrialAvailable: Boolean,

    @ColumnInfo(name = FIELD_TYPE)
    val type: Type,

    @ColumnInfo(name = FIELD_YEARLY_PER_MONTH_PRICE)
    val yearlyPerMonthPrice: String?,

    @ColumnInfo(name = FIELD_PER_MONTH_PRICE)
    val perMonthPrice: String?,

    @ColumnInfo(name = FIELD_DISCOUNT)
    val discount: Int?,
) {
    enum class Type(val id: String) {
        MONTHLY("monthly"),
        YEARLY("yearly"),
        PERMANENT("permanent");

        companion object {
            fun fromId(id: String): Type = when (id) {
                "monthly" -> MONTHLY
                "yearly" -> YEARLY
                "permanent" -> PERMANENT
                else -> throw IllegalStateException()
            }
        }
    }

    companion object {
        const val TABLE_NAME = "BillingPlans"
        const val FIELD_ID = "id"
        const val FIELD_PRICE = "price"
        const val FIELD_ID_TRIAL_AVAILABLE = "is_trial_available"
        const val FIELD_TYPE = "type"
        const val FIELD_YEARLY_PER_MONTH_PRICE = "yearly_per_month_price"
        const val FIELD_PER_MONTH_PRICE = "per_month_price"
        const val FIELD_DISCOUNT = "discount"
    }
}