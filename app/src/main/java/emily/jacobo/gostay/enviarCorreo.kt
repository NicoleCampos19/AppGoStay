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

suspend fun enviarCorreo(receptor: String, sujeto: String, mensaje: String) = withContext(
    Dispatchers.IO) {

    val props = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587") // Cambiamos a 587 para STARTTLS
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true") // Habilitamos STARTTLS
        put("mail.smtp.ssl.protocols", "TLSv1.2") // Especificamos TLSv1.2
    }

    val session = Session.getInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("gostay2024@gmail.com", "dekt szbp iwoe swut")
        }
    })


    // Hacemos el envío
    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("gostay2024@gmail.com"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor))
            setSubject(sujeto)
            setContent(mensaje, "text/html; charset=utf-8")
        }

        Transport.send(message)
        println("Correo enviado satisfactoriamente")
    }

    // Muestra el error en caso que se de
    catch (e: MessagingException) {
        e.printStackTrace()
        println("CORREO NO ENVIADO EXE")
    }
}
