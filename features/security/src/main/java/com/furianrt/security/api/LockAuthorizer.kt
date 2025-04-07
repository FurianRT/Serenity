package com.furianrt.security.api

import kotlinx.coroutines.flow.Flow

interface LockAuthorizer {
    fun isAuthorized(): Flow<Boolean>
    fun authorize()
}