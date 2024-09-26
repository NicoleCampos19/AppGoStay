package emily.jacobo.gostay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.security.MessageDigest

class activity_iniciar_sesion : AppCompatActivity() {

    companion object variableGloalLogin {
        const val InicioSesionGoogle = 100
        lateinit var correoIngresado: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        val txtCorreoIniciarSesion = findViewById<EditText>(R.id.txtCorreoRecu)
        val txtContrasenaIniciarSesion = findViewById<EditText>(R.id.txtContrasenaIniciarSesion)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)

        imvAtrasc.setOnClickListener {
            val intent = Intent(this, activity_registrarse::class.java)
            startActivity(intent)
        }

        // Verificar si el usuario ya está logueado
        val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val isLoggedIn = userPreferences.getBoolean("IsLogedIn", false)

        if (isLoggedIn) {
            // Inicializar la variable global `correoIngresado` desde SharedPreferences
            correoIngresado = userPreferences.getString("email", "") ?: ""
            val intent = Intent(this, PaginaInicio::class.java)
            startActivity(intent)
            finish()
        }

        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnIniciar.setOnClickListener {
            correoIngresado = txtCorreoIniciarSesion.text.toString().trim()
            val clave = txtContrasenaIniciarSesion.text.toString().trim()

            if (correoIngresado.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!correoIngresado.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoIniciarSesion.error = "El correo no tiene un formato válido"
                return@setOnClickListener
            }

            if (clave.length <= 12) {
                txtContrasenaIniciarSesion.error = "La contraseña debe tener al menos 12 caracteres"
                return@setOnClickListener
            }

            val contrasenaEncriptada = hashSHA256(clave)

            CoroutineScope(Dispatchers.IO).launch {
                val conexion = ClaseConexion().cadenaConexion()

                val query = "SELECT tu.nombre_usuario FROM tbTiposUsuarios tu INNER JOIN tbUsuarios u ON tu.id_tipo_usuario = u.id_tipo_usuario WHERE u.correo = ? AND u.contraseña = ?"
                val statement = conexion?.prepareStatement(query)
                statement?.setString(1, correoIngresado)
                statement?.setString(2, contrasenaEncriptada)
                val resultSet = statement?.executeQuery()

                if (resultSet?.next() == true) {
                    // Guardar estado de sesión en SharedPreferences
                    val editor = userPreferences.edit()
                    editor.putBoolean("IsLogedIn", true)
                    editor.putString("email", correoIngresado)
                    editor.apply()

                    // Redirigir a PaginaInicio
                    val intent = Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@activity_iniciar_sesion,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}