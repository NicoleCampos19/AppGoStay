package emily.jacobo.gostay

import android.annotation.SuppressLint
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

class Confirmacion_Cuenta : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacion_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val txtCodigoConf = findViewById<TextView>(R.id.txtCodigo)
        val btnConfirmaCuenta = findViewById<Button>(R.id.btnRecuperacion)
        val btnReenviar = findViewById<Button>(R.id.btnReenviar)
        val codigoRecuperacion = RecuperacionCuentaActivity.variablesGobalesRecuperacion.codigoRecuperacion.toString().trim()
        val Correo = RecuperacionCuentaActivity.variablesGobalesRecuperacion.Correo

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, RecuperacionCuentaActivity::class.java)
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

        btnReenviar.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                enviarCorreo(
                    "${Correo}",
                    "Recuperacion de contraseña",
                    "Este es tu código de recuperación de cuenta $codigoRecuperacion" )


            }
        }

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, RecuperacionCuentaActivity::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }
/*
        btnConfirmaCuenta.setOnClickListener {

            val codigo = txtCodigoConf.text.toString()

            var hayVacios = false
            var hayErrores = false

            if(codigo.isEmpty()){
                setErrorWithCustomFont(txtCodigoConf, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (codigo.length != 6) {
                setErrorWithCustomFont(txtCodigoConf, "El código debe contener 6 carácteres", R.font.poppins)
                hayErrores = true
            }
            // Si hay errores, no procede a guardar los datos
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG)
            } else{



                    //try {
                        val codigoIngresado = txtCodigoConf.text.toString().toInt()

                        if (codigoIngresado == codigoRecuperacion) {
                            val siguientepantalla = Intent(this, CreacionContrasenaActivity::class.java)
                            startActivity(siguientepantalla)
                        } else if (codigoIngresado != codigoRecuperacion) {
                            Toast.makeText(this, "Código Incorrecto", Toast.LENGTH_SHORT).show()
                           // val siguientepantalla = Intent(this, RecuperacionCuentaActivity::class.java)
                           // startActivity(siguientepantalla)
                        }
                   /* } catch (e: NumberFormatException) {
                        // Manejar el caso donde el texto ingresado no es un número válido
                        Toast.makeText(this, "Ingrese un código válido", Toast.LENGTH_SHORT).show()
                    }*/



                    val siguientepantalla = Intent(this, CreacionContrasenaActivity::class.java)
                    startActivity(siguientepantalla)
                    overridePendingTransition(0, 0)

            }
        }*/

        Log.d("Confirmacion_Cuenta", "ANTES DEL METODO DE CONFIRMAR")
        btnConfirmaCuenta.setOnClickListener {


            Log.d("Confirmacion_Cuenta", "BOTÓN confirmar presionado")
            val codigo = txtCodigoConf.text.toString()

            Log.d("Confirmacion_Cuenta", "Código INGRESADO: $codigoRecuperacion")




            var hayVacios = false
            var hayErrores = false

            if (codigo.isEmpty()) {
                setErrorWithCustomFont(txtCodigoConf, "Llena este campo", R.font.poppins)
                hayVacios = true
            } else if (codigo.length != 6) {
                setErrorWithCustomFont(txtCodigoConf, "El código debe contener 6 carácteres", R.font.poppins)
                hayErrores = true
            }

            // Si hay errores, no procede a guardar los datos
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG).show()
            } else {
                // Intentar comparar el código ingresado con el de recuperación
                try {
                    // Asegúrate de que el código de recuperación es un String


                    Log.d("Confirmacion_Cuenta", "DESPUES DEL METODO DE CONFIRMAR")

                    if (codigo.trim() == codigoRecuperacion.trim()) {

                        Log.d("Confirmacion_Cuenta", "DENTRO DEL METODO DE CONFIRMAR")
                        Log.d("Confirmacion_Cuenta", "Código de recuperación: $codigoRecuperacion")

                        val siguientepantalla = Intent(this, CreacionContrasenaActivity::class.java)
                        startActivity(siguientepantalla)
                        overridePendingTransition(0, 0)
                    } else {
                        Toast.makeText(this, "Código Incorrecto", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Manejar cualquier excepción
                    Toast.makeText(this, "Error inesperado. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        }

    }


