package com.furianrt.domain.managers

import kotlinx.coroutines.flow.Flow

interface LockAuthorizer {
    fun isAuthorized(): Flow<Boolean>
    fun authorize()
    fun skipNextLock()
    fun cancelSkipNextLock()
}