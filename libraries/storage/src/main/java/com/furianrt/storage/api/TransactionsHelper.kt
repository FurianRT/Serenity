package com.furianrt.storage.api

interface TransactionsHelper {
    suspend fun <R> startTransaction(block: suspend () -> R): R
}
