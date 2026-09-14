package com.furianrt.reminders.api

import android.content.Intent

interface RemindersApi {
    fun cancelNotification(intent: Intent)
}