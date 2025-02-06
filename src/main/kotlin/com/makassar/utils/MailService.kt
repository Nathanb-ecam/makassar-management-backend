package com.makassar.utils

import io.ktor.server.application.*
import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import java.util.Properties

object MailService {

    private val logger = LoggerFactory.getLogger("MailService")

    private lateinit var smtpHost: String
    private lateinit var smtpPort: String
    private lateinit var emailUsername: String
    private lateinit var emailPassword: String
    private lateinit var backendIP : String
    private lateinit var backendPort : String

    fun init(environment: ApplicationEnvironment) {
        smtpHost = environment.config.propertyOrNull("ktor.appConfig.environment.mail.smtpHost")?.getString() ?: "smtp.gmail.com"
        smtpPort = environment.config.propertyOrNull("ktor.appConfig.environment.mail.smtpPort")?.getString() ?: "587"
        emailUsername = environment.config.propertyOrNull("ktor.appConfig.environment.mail.emailUsername")?.getString() ?: ""
        emailPassword = environment.config.propertyOrNull("ktor.appConfig.environment.mail.emailPassword")?.getString() ?: ""
        backendIP = environment.config.propertyOrNull("ktor.deployment.domain")?.getString() ?: "localhost"
        backendPort = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
        logger.info("SMTP USER: $emailUsername")
    }

    fun sendOtpEmail(toEmail: String, otp: String) : Boolean {
        //val verificationLink = "http://localhost:8080/api/verify?mail=$toEmail&otp=$otp"
        //val verificationLink =  "http://localhost:8080/api/verify"
        val verificationLink =  "http://$backendIP:$backendPort/api/verify"
        logger.info("Verification link: $verificationLink")

        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(emailUsername, emailPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(emailUsername))
                setRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
                subject = "Verify Your Account"

                val htmlContent = """
                <html>
                    <body>
                        <h2>Verify Your Account</h2>
                        <p>Click the button below to verify your account:</p>          
                       <a href="$verificationLink?mail=$toEmail&otp=$otp"
                          style="background-color:#28a745;color:white;padding:10px 15px;text-decoration:none;border-radius:5px;">
                          Verify Your Account
                       </a>

                        <p>If you didn't request this, please ignore this email.</p>
                    </body>
                </html>
            """.trimIndent()

                setContent(htmlContent, "text/html; charset=utf-8")
            }

 /*           <form method="post" action="$verificationLink">
            <input type="hidden" name="mail" value="$toEmail" />
            <input type="hidden" name="otp" value="$otp" />
            <button type="submit">Verify</button>
            </form>*/


            Transport.send(message)
            println("Verification email sent to $toEmail")
            return true
        } catch (e: MessagingException) {
            e.printStackTrace()
            return false
        }
    }
}
