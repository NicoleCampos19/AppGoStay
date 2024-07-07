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

        class VerifyCodeActivity : AppCompatActivity() {

            private lateinit var txtCorreoInciarSesion : TextView
            private lateinit var btnConfirmaCuenta: Button
            private var codeEditText: EditText? = null
            private var databaseReference: DatabaseReference? = null
            private lateinit var btnReenviar: Button



            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_confirmacion_cuenta)

                // Inicializar vistas
                txtCorreoInciarSesion = findViewById(R.id.txtCorreoInciarSesion)
                btnConfirmaCuenta = findViewById(R.id.btnConfirmaCuenta)
                btnReenviar = findViewById(R.id.btnReenviar)
                databaseReference = FirebaseDatabase.getInstance().getReference()

                codeEditText = findViewById(R.id.txtCorreoInciarSesion);
                databaseReference = FirebaseDatabase.getInstance().getReference("verificationCodes");


                // Configurar onClickListener para el botón de Confirmar Cuenta
                btnConfirmaCuenta.setOnClickListener {
                    val code = btnConfirmaCuenta.getText().toString().trim { it <= ' ' }
                    if (!code.isEmpty()) {
                        verifycode(code)
                    } else {
                        Toast.makeText(
                            this@VerifyCodeActivity,
                            "Por favor, introduce el código",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // Configurar onClickListener para el botón de Reenviar código
                btnReenviar.setOnClickListener{
                    val email = txtCorreoInciarSesion.text.toString().trim()
                    resendVerificationCode(email)
                }


            }

            // Función para reenviar el correo electrónico de verificación al usuario en caso de que no lo haya recibido
            private fun resendVerificationCode(email: String) {

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                            finish() // Regresar a la pantalla anterior después de enviar el correo
                        } else {
                            Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            // Función para verificar que el código sea igual al enviado al correo
            private fun verifycode(code: String) {

                val email = txtCorreoInciarSesion.text.toString().trim()
                val correctCode = String
                if (code.equals(correctCode)) {
                    val intent = Intent(
                        this@Confirmacion_Cuenta,
                        CreacionContrasenaActivity::class.java
                    )

                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                } else {
                    Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                }

            }



        }

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnConfirmaCuenta = findViewById<Button>(R.id.btnConfirmaCuenta)

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