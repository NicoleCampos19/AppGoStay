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

        class ForgotPasswordActivity : AppCompatActivity() {

            private lateinit var emailEditText: TextView
            private lateinit var resetButton: Button


            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_recuperacion_cuenta)

                // Inicializar vistas
                emailEditText = findViewById(R.id.txtCorreo)
                resetButton = findViewById(R.id.btnRecuperacion)


                // Configurar onClickListener para el botón de Recuperación
                resetButton.setOnClickListener {
                    val email = emailEditText.text.toString().trim()

                    if (email.isEmpty()) {
                        Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Enviar correo de reseteo de contraseña
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Correo enviado! Revisa tu bandeja de entrada", Toast.LENGTH_SHORT).show()
                                finish() // Regresar a la pantalla anterior después de enviar el correo
                            } else {
                                Toast.makeText(this, "Error al enviar correo de recuperación. Verifica el correo ingresado", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        val imvAtras = findViewById<ImageView>(R.id.imvAtras)
        val btnRecuperacion = findViewById<Button>(R.id.btnRecuperacion)

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