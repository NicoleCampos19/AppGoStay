package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecuperacionCuentaActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacion_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val imvAtras = findViewById<ImageView>(R.id.imvAtras)
        val btnRecuperacion = findViewById<Button>(R.id.btnRecuperacion)

        btnRecuperacion.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val codigoRecuperacion = (100000..999999).random()
                enviarCorreo("verosofpalacioss@gmail.com", "Recuperacion de contraseña", "Este es el codigo de recuperacion $codigoRecuperacion" )
            }
        }

        imvAtras.setOnClickListener {
            val volverAtras = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }

        btnRecuperacion.setOnClickListener {
            val siguientepantalla = Intent(this, Confirmacion_Cuenta::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }
    }

}