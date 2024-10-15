package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class metodos_contras : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_metodos_contras)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mandar a llamar a los elementos de la vista
        val btnRecuCorreo = findViewById<Button>(R.id.btnRecuCorreo)
        val btnRecuNum = findViewById<Button>(R.id.btnRecuNum)
        val btnAtras = findViewById<ImageView>(R.id.imvAtras)

        // Para poder ir a activity_iniciar_sesion
        btnAtras.setOnClickListener {
            val volverAtras = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(volverAtras)
        }

        // Para poder ir a RecuperacionCuentaActivity
        btnRecuCorreo.setOnClickListener {
            val siguientepantallita = Intent(this, RecuperacionCuentaActivity::class.java)
            startActivity(siguientepantallita)
        }

        // Para poder ir a recuperacion_cuenta_cel
        btnRecuNum.setOnClickListener {
            val siguientepantallita = Intent(this, RecuperacionCuentaCel::class.java)
            startActivity(siguientepantallita)
        }
    }
}