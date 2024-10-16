package emily.jacobo.gostay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class contacto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacto)

        val edtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val edtMensaje = findViewById<EditText>(R.id.txtMensaje)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)

        //Navegación para ir a la activity anterior
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Configuraciones::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0,0)

        }

        btnEnviar.setOnClickListener {
            val tituloUsuario = edtCorreo.text.toString().trim()
            val mensajeUsuario = edtMensaje.text.toString().trim()

            if (tituloUsuario.isEmpty() || mensajeUsuario.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

                enviarCorreo(tituloUsuario, mensajeUsuario)
            overridePendingTransition(0,0)

        }
    }

    private fun enviarCorreo(tituloUsuario: String, mensaje: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Solo usar "mailto:"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("equipogostay@gmail.com")) // Correo de destino
            putExtra(Intent.EXTRA_SUBJECT, tituloUsuario) // Título del correo
            putExtra(Intent.EXTRA_TEXT, mensaje) // Solo el mensaje
            putExtra(Intent.EXTRA_CC, "equipogostay@gmail.com")
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir la aplicación de correo", Toast.LENGTH_LONG).show()
        }
    }
}