package emily.jacobo.gostay

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class creacion_contrasena_cel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creacion_contrasena_cel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se mandan a llamar los elementos de la vista
        val btnCrearContrasena = findViewById<Button>(R.id.btnCrearcontrasena)
        val  txtContraseñaNueva = findViewById<EditText>(R.id.txtContraseñaNueva)

        //Validación para campos con la fuente de poppins
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

        btnCrearContrasena.setOnClickListener {

            // Variable que contiene la contraseña
            val contrasena = txtContraseñaNueva.text.toString()

            // Variables que captan los posibles errores
            var hayVacios = false
            var hayErrores = false

            //Para el campo no sea vacío
             if(contrasena.isEmpty()){
            setErrorWithCustomFont(txtContraseñaNueva, "Llena este campo", R.font.poppins)
            hayVacios = true
        }

        // Que la conytaseña no contenga más de 12 carácteres
        else if (contrasena.length < 12) {
            setErrorWithCustomFont(txtContraseñaNueva, "La contraseña debe contener 12 carácteres", R.font.poppins)
            hayErrores = true
        }

        }

    }
}