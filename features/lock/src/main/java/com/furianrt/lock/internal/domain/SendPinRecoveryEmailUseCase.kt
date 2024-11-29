package com.furianrt.lock.internal.domain

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.SecurityRepository
import com.furianrt.lock.BuildConfig
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

        val session = Session.getDefaultInstance(
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
        message.subject = subject
        message.setText(text)
        try {
            Result.success(Transport.send(message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}