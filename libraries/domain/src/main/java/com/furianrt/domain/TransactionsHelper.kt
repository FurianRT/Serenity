package com.furianrt.domain

interface TransactionsHelper {
    suspend fun startTransaction(block: suspend () -> Unit)
}
