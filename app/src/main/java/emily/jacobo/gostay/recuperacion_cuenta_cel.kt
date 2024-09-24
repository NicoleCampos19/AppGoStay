package emily.jacobo.gostay

import android.content.Intent
import android.os.Build
import android.os.Bundle
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

class recuperacion_cuenta_cel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacion_cuenta_cel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imvAtras = findViewById<ImageView>(R.id.imvAtras)
        val editTextNumber = findViewById<EditText>(R.id.editTextNumber)
        val btnRecuperacionCel = findViewById<Button>(R.id.btnRecuperacionCel)

        //Navegación
        imvAtras.setOnClickListener {
            val volverAtras = Intent(this, metodos_contras::class.java)
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
        btnRecuperacionCel.setOnClickListener {
            val numero = editTextNumber.text.toString()

            var hayVacios = false
            var hayErrores = false

            //Para el campo de correo
            if(numero.isEmpty()){
                setErrorWithCustomFont(editTextNumber, "Llena este campo", R.font.poppins)
                hayVacios = true

            }
            else if (numero.length != 8) {
                setErrorWithCustomFont(editTextNumber, "El teléfono debe contener 8 carácteres", R.font.poppins)
                hayErrores = true
            }
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG)
            }else{
                btnRecuperacionCel.setOnClickListener {
                    val continuar = Intent(this, confirmacion_cuenta_cel::class.java)
                    startActivity(continuar)
                    overridePendingTransition(0, 0)
                }
            }
    }
    }
}