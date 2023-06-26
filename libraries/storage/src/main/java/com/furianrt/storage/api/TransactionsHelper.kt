package com.furianrt.storage.api

interface TransactionsHelper {
    suspend fun <R> withTransaction(block: suspend () -> R): R
}
