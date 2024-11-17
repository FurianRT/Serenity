package com.furianrt.lock.internal.domain

import android.util.Patterns
import javax.inject.Inject

internal class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}