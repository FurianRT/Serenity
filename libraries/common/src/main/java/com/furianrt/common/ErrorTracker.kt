package com.furianrt.common

interface ErrorTracker {
    fun trackNonFatalError(error: Throwable)
}