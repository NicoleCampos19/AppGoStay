package emily.jacobo.gostay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.*
import java.io.IOException
import kotlin.random.Random

class RecuperacionCuentaCel : AppCompatActivity() {

    private lateinit var txtNumeroCel: EditText
    private lateinit var btnRecuperacionCel: Button

    private val ACCOUNT_SID = "AC72c5823cab688199a114d6cded825545"
    private val AUTH_TOKEN = "c9080d72383e232b3057abef1bc3f797"
    private val FROM_NUMBER = "+50370294530" // Número Twilio
    private val numeroDestino = "+50371741555" // Número de destino fijo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacion_cuenta_cel)

        // Ajusta los márgenes en función de las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa los elementos de la vista
        val imvAtras = findViewById<ImageView>(R.id.imvAtras)
        txtNumeroCel = findViewById(R.id.txtNumeroCel)
        btnRecuperacionCel = findViewById(R.id.btnRecuperacionCel)

        imvAtras.setOnClickListener {
            startActivity(Intent(this, metodos_contras::class.java))
            overridePendingTransition(0, 0)
        }

        btnRecuperacionCel.setOnClickListener {
            validarYEnviarSMS()
        }
    }

    private fun validarYEnviarSMS() {
        val numero = txtNumeroCel.text.toString().trim()

        if (numero.isEmpty()) {
            mostrarError(txtNumeroCel, "Llena este campo")
        } else if (numero.length != 8) {
            mostrarError(txtNumeroCel, "El teléfono debe contener 8 carácteres")
        } else {
            val numeroCompleto = numeroDestino
            enviarSMS(numeroCompleto)
            Log.d("TAG", "ESTE ES EL NUMEROOOOO $numeroCompleto")

        }
    }

    private fun mostrarError(editText: TextView, mensaje: String) {
        val typeface = ResourcesCompat.getFont(this, R.font.poppins)
        val spannableString = android.text.SpannableString(mensaje)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Usar TypefaceSpan con Typeface para API 28+
            typeface?.let {
                spannableString.setSpan(
                    android.text.style.TypefaceSpan(it),
                    0, spannableString.length,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            // Usar StyleSpan como alternativa para API < 28
            spannableString.setSpan(
                android.text.style.StyleSpan(typeface?.style ?: android.graphics.Typeface.NORMAL),
                0, spannableString.length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        editText.error = spannableString
    }

    private fun enviarSMS(numeroDestino: String) {
        // Generar un código aleatorio de 6 dígitos
        val codigoRandom = (100000..999999).random().toString()

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("To", numeroDestino)
            .add("From", FROM_NUMBER)
            .add("Body", "Tu código de verificación es: $codigoRandom")
            .build()

        Log.d("TAST","ESTE ES EL NUMERO AL QUE LO ENVIA $numeroDestino")

        val request = Request.Builder()
            .url("https://api.twilio.com/2010-04-01/Accounts/$ACCOUNT_SID/Messages.json")
            .post(requestBody)
            .addHeader("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RecuperacionCuentaCel, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RecuperacionCuentaCel,
                            "SMS enviado a $numeroDestino con código: $codigoRandom",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a la pantalla de confirmación si el mensaje se envió correctamente
                        val continuar = Intent(this@RecuperacionCuentaCel, confirmacion_cuenta_cel::class.java)
                        startActivity(continuar)
                        overridePendingTransition(0, 0)
                    } else {
                        // Mostrar el mensaje de error devuelto por Twilio
                        val errorMessage = response.body?.string() ?: "Error desconocido"
                        Toast.makeText(this@RecuperacionCuentaCel, "Error al enviar SMS: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.d("TAG", "ESTE ES EL ERRRORRR $errorMessage")

                    }
                }
            }
        })
    }

}
