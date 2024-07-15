package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import emily.jacobo.gostay.RecuperacionCuentaActivity.variablesGobalesRecuperacion.Correo
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
        val txtCodigoConf = findViewById<TextView>(R.id.txtCodigoConf)
        val btnConfirmaCuenta = findViewById<Button>(R.id.btnConfirmaCuenta)
        val btnReenviar = findViewById<Button>(R.id.btnReenviar)
        val codigoRecuperacion = RecuperacionCuentaActivity.variablesGobalesRecuperacion.codigoRecuperacion
        val Correo = RecuperacionCuentaActivity.variablesGobalesRecuperacion.Correo


        btnReenviar.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                enviarCorreo(
                    "${Correo}",
                    "Recuperacion de contraseña",
                    "Este es tu código de recuperación de cuenta $codigoRecuperacion" )


            }
        }

        btnConfirmaCuenta.setOnClickListener {
            try {
                val codigoIngresado = txtCodigoConf.text.toString().toInt()

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
            val volverAtras = Intent(this, RecuperacionCuentaActivity::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }

        btnConfirmaCuenta.setOnClickListener {
            val siguientepantalla = Intent(this, CreacionContrasenaActivity::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }
    }
}