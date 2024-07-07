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
import com.google.firebase.auth.FirebaseUser

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

        class ForgotPasswordActivity : AppCompatActivity() {

            private lateinit var txtNewContra: TextView
            private lateinit var btnCrearcontrasena: Button


            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_creacion_contrasena)

                // Inicializar vistas
                txtNewContra = findViewById(R.id.txtNewContra)
                btnCrearcontrasena = findViewById(R.id.btnCrearcontrasena)

                // Configurar onClickListener para el botón de Crear una Nueva Contraseña
                btnCrearcontrasena.setOnClickListener{
                    val newPassword: String = txtNewContra.getText().toString().trim()
                    if (!newPassword.isEmpty()) {
                        changePassword(newPassword)
                    } else {
                        Toast.makeText(this, "Por favor, introduce una nueva contraseña", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            //Función para permitir que el usuario cambie su contraseña
            private fun changePassword(newPassword: String) {

                val user = FirebaseUser.NULL

                FirebaseAuth.getInstance().sendPasswordResetEmail(user)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                            finish() // Regresar a la pantalla anterior después de enviar el correo
                        } else {
                            Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnCrearContrasena = findViewById<Button>(R.id.btnCrearcontrasena)

        btnCrearContrasena.setOnClickListener {
            val CrearContrasena = Intent(this, PaginaInicio::class.java)
            startActivity(CrearContrasena)
            overridePendingTransition(0, 0)
        }
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Confirmacion_Cuenta::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }
    }
}