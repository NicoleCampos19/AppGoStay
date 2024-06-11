package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreacionContrasenaActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creacion_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnCrearContrasena = findViewById<Button>(R.id.btnCrearcontrasena)

        btnCrearContrasena.setOnClickListener {
            val CrearContrasena = Intent(this, PaginaInicio::class.java)
            startActivity(CrearContrasena)
        }
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Confirmacion_Cuenta::class.java)
            startActivity(volverAtras)
        }
    }
}