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

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val txtCodigoCorreo = findViewById<TextView>(R.id.txtCodigoCorreo)
        val btnConfirmaCorreo = findViewById<Button>(R.id.btnConfirmaCorreo)
        val btnReenviarExis = findViewById<Button>(R.id.btnReenviarExis)
        val codigoRegis = activity_registrarse.variableGloalLogin.CodigoRegis.toString().trim()
        val txtCorreoI = activity_registrarse.variableGloalLogin.txtCorreoI


        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }

        btnReenviarExis.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                enviarCorreo(
                    "${txtCorreoI}",
                    "Recuperacion de contraseña",
                    "Este es tu código de recuperación de cuenta $codigoRegis" )


            }
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

        Log.d("Confirmacion_Cuenta", "ANTES DEL METODO DE CONFIRMAR REGIS")
        btnConfirmaCorreo.setOnClickListener {

            Log.d("Confirmacion_Cuenta", "BOTÓN confirmar presionado REGIS")

            val codigo = txtCodigoCorreo.text.toString()

            Log.d("Confirmacion_Cuenta", "Código INGRESADO: $codigoRegis")

            var hayVacios = false
            var hayErrores = false

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
            } else{


                try {
                    // Asegúrate de que el código de recuperación es un String


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
        }
    }

}
