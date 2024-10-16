package emily.jacobo.gostay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class activity_ConfirmarCorreo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_correo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se llaman los elementos que se encuentran en la vista
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val txtCodigoCorreo = findViewById<TextView>(R.id.txtCodigoCorreo)
        val btnConfirmaCorreo = findViewById<Button>(R.id.btnConfirmaCorreo)
        val btnReenviarExis = findViewById<Button>(R.id.btnReenviarExis)
        val codigoRegis = activity_registrarse.variableGloalLogin.CodigoRegis.toString().trim()
        val txtCorreoI = activity_registrarse.variableGloalLogin.txtCorreoI

        // Para poder regresar a la pantalla anterior
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }

        // Configura el evento de clic para el botón "Reenviar"
        btnReenviarExis.setOnClickListener {
         // Inicia una nueva coroutine en el contexto principal
            CoroutineScope(Dispatchers.Main).launch {
                // Llama a la función para enviar un correo de recuperación de contraseña
                enviarCorreo(
                    "${txtCorreoI}", //Correo del usuario
                    "Recuperacion de contraseña", //Asunto del correo
                    "Este es tu código de recuperación de cuenta $codigoRegis" ) // Código del correo
            }
            overridePendingTransition(0,0)

        }

        //Validación para campos
        @RequiresApi(Build.VERSION_CODES.P)
        fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
            val typeface = ResourcesCompat.getFont(this, fontResId)
            val spannableString = android.text.SpannableString(errorMessage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spannableString.setSpan(
                    typeface?.let { android.text.style.TypefaceSpan(it) }, 0, spannableString.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            editText.error = spannableString
        }

        // Registra un mensaje en el log antes de confirmar el registro
        Log.d("Confirmacion_Cuenta", "ANTES DEL METODO DE CONFIRMAR REGIS")
        // Configura el evento de clic para el botón de confirmación de correo
        btnConfirmaCorreo.setOnClickListener {
        // Registra un mensaje cuando se presiona el botón
            Log.d("Confirmacion_Cuenta", "BOTÓN confirmar presionado REGIS")

            // Obtiene el código ingresado por el usuario
            val codigo = txtCodigoCorreo.text.toString()

            // Registra el código ingresado en el log
            Log.d("Confirmacion_Cuenta", "Código INGRESADO: $codigoRegis")

            var hayVacios = false // Indica si hay campos vacíos
            var hayErrores = false // Indica si hay errores en el formato

            //Verifica si los campos están vacíos o si cumplen con condiciones y asigna acciones
            if(codigo.isEmpty()){
                setErrorWithCustomFont(txtCodigoCorreo, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (codigo.length != 6) {
                setErrorWithCustomFont(txtCodigoCorreo, "El código debe contener 6 carácteres", R.font.poppins)
                hayErrores = true
            }
            // Si hay errores, no procede a guardar los datos
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG)
                //Si las condiciones se cumplen pasa lo siguiente
            } else{
                try {
                    Log.d("Confirmacion_Cuenta", "DESPUES DEL METODO DE CONFIRMAR REGIS")
                    if (codigo.trim() == codigoRegis.trim()) {
                        Log.d("Confirmacion_Cuenta", "DENTRO DEL METODO DE CONFIRMAR REGIS")
                        Log.d("Confirmacion_Cuenta", "Código de recuperación: $codigoRegis")
                        val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
                        startActivity(siguientepantalla)
                        overridePendingTransition(0, 0)
                    } else {
                        Toast.makeText(this, "Código Incorrecto", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Confirmacion_Cuenta", "Error: ${e.message}")
                    Toast.makeText(this, "Error inesperado. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                }
            }
            overridePendingTransition(0,0)

        }
    }

}
