package com.furianrt.common

interface ErrorTracker {
    fun trackNonFatalError(error: Throwable)

    companion object {
        fun printStackTrace(error: Throwable) {
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }
}