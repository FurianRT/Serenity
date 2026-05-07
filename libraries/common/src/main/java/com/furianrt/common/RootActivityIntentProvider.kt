package com.furianrt.common

import android.content.Intent

interface RootActivityIntentProvider {
    fun provide(): Intent
}