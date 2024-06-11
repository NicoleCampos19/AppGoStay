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
        val btnConfirmaCuenta = findViewById<Button>(R.id.btnConfirmaCuenta)

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, RecuperacionCuentaActivity::class.java)
            startActivity(volverAtras)
        }

        btnConfirmaCuenta.setOnClickListener {
            val siguientepantalla = Intent(this, CreacionContrasenaActivity::class.java)
            startActivity(siguientepantalla)
        }
    }
}