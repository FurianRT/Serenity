package com.furianrt.backup.api

import android.content.Intent

interface RootActivityIntentProvider {
    fun provide(): Intent
}