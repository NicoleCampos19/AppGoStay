package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        val btnConfirmarCuenta = findViewById<Button>(R.id.btnConfirmaCuenta)

        btnConfirmarCuenta.setOnClickListener {
            val siguientepantalla = Intent(this, creacion_contrasena_cel::class.java)
            startActivity(siguientepantalla)
        }
    }
}