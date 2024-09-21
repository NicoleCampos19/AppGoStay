package emily.jacobo.gostay

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
        val codigoRecuperacion = RecuperacionCuentaActivity.variablesGobalesRecuperacion.codigoRecuperacion
       val txtCorreoI = activity_registrarse.variableGloalLogin.txtCorreoI


        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
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

        btnConfirmaCorreo.setOnClickListener {

            val codigo = txtCodigoCorreo.text.toString()

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
                btnReenviarExis.setOnClickListener {

                    CoroutineScope(Dispatchers.Main).launch {
                        enviarCorreo(
                            "${txtCorreoI}",
                            "Recuperacion de contraseña",
                            "Este es tu código de recuperación de cuenta $codigoRecuperacion" )


                    }
                }

                btnConfirmaCorreo.setOnClickListener {
                    try {
                        val codigoIngresado = txtCodigoCorreo.text.toString().toInt()

                        if (codigoIngresado == codigoRecuperacion) {
                            val siguientepantalla = Intent(this, CreacionContrasenaActivity::class.java)
                            startActivity(siguientepantalla)
                        } else if (codigoIngresado != codigoRecuperacion) {
                            Toast.makeText(this, "Código Incorrecto", Toast.LENGTH_SHORT).show()
                            val siguientepantalla = Intent(this, RecuperacionCuentaActivity::class.java)
                            startActivity(siguientepantalla)
                        }
                    } catch (e: NumberFormatException) {
                        // Manejar el caso donde el texto ingresado no es un número válido
                        Toast.makeText(this, "Ingrese un código válido", Toast.LENGTH_SHORT).show()
                    }
                }
                imvAtrasc.setOnClickListener {
                    val volverAtras = Intent(this, activity_registrarse::class.java)
                    startActivity(volverAtras)
                    overridePendingTransition(0, 0)
                }
                btnConfirmaCorreo.setOnClickListener {
                    val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
                    startActivity(siguientepantalla)
                    overridePendingTransition(0, 0)
                }
            }
        }
    }

}

