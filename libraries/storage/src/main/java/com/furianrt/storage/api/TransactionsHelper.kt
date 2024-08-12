package com.furianrt.storage.api

interface TransactionsHelper {
    suspend fun startTransaction(block: suspend () -> Unit)
}
