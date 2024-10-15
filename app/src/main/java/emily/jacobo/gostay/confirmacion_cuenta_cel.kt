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

class confirmacion_cuenta_cel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacion_cuenta_cel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se mandan a llamar los elementos de la vista
        val btnRecuperacionCel = findViewById<Button>(R.id.btnRecuperacionCel)
        val imvAtrascel = findViewById<ImageView>(R.id.imvAtrascel)
        val txtCodigoCel = findViewById<EditText>(R.id.txtCodigoCel)

        // Navegación para volver atrás
        imvAtrascel.setOnClickListener {
            val volverAtras = Intent(this, RecuperacionCuentaCel::class.java)
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

            // Variable para validar el código
            val codigocel = txtCodigoCel.text.toString()

            // Variables que captan los posibles errores
            var hayVacios = false
            var hayErrores = false

            // Si el código esta vacío se muestra una alertita
            if(codigocel.isEmpty()){
                setErrorWithCustomFont(txtCodigoCel, "Llena este campo", R.font.poppins)
                hayVacios = true
            }

            // No permite que el código sea mayor a 6
            else if (codigocel.length != 6) {
                setErrorWithCustomFont(txtCodigoCel, "El código debe contener 6 carácteres", R.font.poppins)
                hayErrores = true
            }
            // Si hay errores, no procede a guardar los datos
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG)
            } else{
                btnRecuperacionCel.setOnClickListener {
                    val siguientepantalla = Intent(this, creacion_contrasena_cel::class.java)
                    startActivity(siguientepantalla)
                }
    }
    }
    }
}