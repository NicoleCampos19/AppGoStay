package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_iniciar_sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtOlvidasteContrasena = findViewById<TextView>(R.id.txtOlvidasteContrasena)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)

        btnIniciar.setOnClickListener {
            val IniciarSesion = Intent(this, PaginaInicio::class.java)
            startActivity(IniciarSesion)
        }

        txtOlvidasteContrasena.setOnClickListener {
            val siguientepantalla = Intent(this, RecuperacionCuentaActivity::class.java)
            startActivity(siguientepantalla)
        }

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
            startActivity(volverAtras)
        }

    }
}