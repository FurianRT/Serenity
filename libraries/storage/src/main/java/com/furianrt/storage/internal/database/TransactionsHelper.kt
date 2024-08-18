package com.furianrt.storage.internal.database

internal interface TransactionsHelper {
    suspend fun startTransaction(block: suspend () -> Unit)
}
