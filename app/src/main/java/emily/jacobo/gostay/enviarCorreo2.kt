package emily.jacobo.gostay

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

suspend fun enviarCorreo2(receptor: String, sujeto: String, mensaje: String) = withContext(
    Dispatchers.IO) {
    // Configuración del servidor SMTP
    val props = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.socketFactory.port", "465")
        put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        put("mail.smtp.auth", "true")
        put("mail.smtp.port", "465")
    }

    val session = Session.getInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("gostay2024@gmail.com", "dekt szbp iwoe swut")
        }
    })

    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("gostay2024@gmail.com"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor))
            setSubject(sujeto)
            setContent(mensaje, "text/html; charset=utf-8")
        }

        Transport.send(message)
        println("Correo enviado satisfactoriamente")
    } catch (e: MessagingException) {
        println("Error de envío de correo: ${e.message}")
        e.printStackTrace()
    }
}
