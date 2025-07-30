package com.furianrt.security.internal.domain

import com.furianrt.common.ErrorTracker
import com.furianrt.core.DispatchersProvider
import com.furianrt.security.BuildConfig
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.inject.Inject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

internal class SendPinRecoveryEmailUseCase @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val errorTracker: ErrorTracker,
    private val dispatchers: DispatchersProvider,
) {
    suspend operator fun invoke(
        subject: String,
        text: String,
    ): Result<Unit> = withContext(dispatchers.io) {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(
            props,
            object : Authenticator() {
                override fun getPasswordAuthentication() = PasswordAuthentication(
                    BuildConfig.SUPPORT_EMAIL,
                    BuildConfig.GMAIL_APP_PASSWORD
                )
            },
        )

        val userEmail = securityRepository.getPinRecoveryEmail().first()
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(BuildConfig.SUPPORT_EMAIL))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail))
        message.setSubject(subject, "UTF-8")
        message.setText(text, "UTF-8")
        try {
            Result.success(Transport.send(message))
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            Result.failure(e)
        }
    }
}