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
        lateinit var correoIngresado: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        // Obtener referencias de los elementos en la UI
        val txtCorreoIniciarSesion = findViewById<EditText>(R.id.txtCorreoRecu)
        val txtContrasenaIniciarSesion = findViewById<EditText>(R.id.txtContrasenaIniciarSesion)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)

        // Obtener SharedPreferences
        val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

        // Verificar si el usuario ya está logueado
        val isLoggedIn = userPreferences.getBoolean("IsLogedIn", false)

        if (isLoggedIn) {
            // Si el usuario ya ha iniciado sesión, cargar correo desde SharedPreferences y redirigir a PaginaInicio
            correoIngresado = userPreferences.getString("email", "") ?: ""
            val intent = Intent(this, PaginaInicio::class.java)
            startActivity(intent)
            finish()
        }

        // Función para encriptar la contraseña usando SHA-256
        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        // Acción al hacer clic en el botón de inicio de sesión
        btnIniciar.setOnClickListener {
            // Obtener el correo y contraseña ingresados por el usuario
            correoIngresado = txtCorreoIniciarSesion.text.toString().trim()
            val clave = txtContrasenaIniciarSesion.text.toString().trim()

            // Validación de campos vacíos
            if (correoIngresado.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de correo
            if (!correoIngresado.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoIniciarSesion.error = "El correo no tiene un formato válido"
                return@setOnClickListener
            }

            // Validar longitud de la contraseña
            if (clave.length <= 12) {
                txtContrasenaIniciarSesion.error = "La contraseña debe tener al menos 12 caracteres"
                return@setOnClickListener
            }

            // Encriptar la contraseña ingresada
            val contrasenaEncriptada = hashSHA256(clave)

            // Iniciar corrutina para ejecutar la consulta de login en segundo plano
            CoroutineScope(Dispatchers.IO).launch {
                val conexion = ClaseConexion().cadenaConexion()

                // Consulta SQL para verificar las credenciales del usuario
                val query = """
                    SELECT tu.nombre_usuario 
                    FROM tbTiposUsuarios tu 
                    INNER JOIN tbUsuarios u 
                    ON tu.id_tipo_usuario = u.id_tipo_usuario 
                    WHERE u.correo = ? AND u.contraseña = ?
                """.trimIndent()

                val statement = conexion?.prepareStatement(query)
                statement?.setString(1, correoIngresado)
                statement?.setString(2, contrasenaEncriptada)
                val resultSet = statement?.executeQuery()

                // Si las credenciales son correctas
                if (resultSet?.next() == true) {
                    // Guardar estado de sesión en SharedPreferences
                    val editor = userPreferences.edit()
                    editor.putBoolean("IsLogedIn", true) // Marcar que el usuario está logueado
                    editor.putString("email", correoIngresado) // Guardar el correo del usuario
                    editor.apply()

                    // Redirigir a PaginaInicio
                    val intent = Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                    startActivity(intent)
                    finish() // Finaliza la activity de login
                } else {
                    // Si las credenciales no son correctas, mostrar un mensaje en el hilo principal
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