package com.furianrt.common

import android.app.Activity

interface ActivityChecker {
    fun isMainActivity(activity: Activity): Boolean
}