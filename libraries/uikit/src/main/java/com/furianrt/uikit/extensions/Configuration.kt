package com.furianrt.uikit.extensions

import android.content.res.Configuration

val Configuration.isTablet: Boolean
    get() = smallestScreenWidthDp >= 600
