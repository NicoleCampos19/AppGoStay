package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Bienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se manda a llamar el btn de la vista
        val btn = findViewById<Button>(R.id.btnContinuarBienvenida)

        // Para ir a la activity de registrarse
        btn.setOnClickListener {
            val siguientepantalla = Intent(this, activity_registrarse::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

    }
}