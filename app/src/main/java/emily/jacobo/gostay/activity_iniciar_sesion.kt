package emily.jacobo.gostay

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.security.MessageDigest

class activity_iniciar_sesion : AppCompatActivity() {

    // Variables globales
    companion object variableGloalLogin {
        var correoIngresado: String = ""
        const val InicioSesionGoogle = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        // Obtener referencias de los elementos en la vista
        val txtCorreoIniciarSesion = findViewById<EditText>(R.id.txtCorreoRecu)
        val txtContrasenaIniciarSesion = findViewById<EditText>(R.id.txtContrasenaIniciarSesion)
        val txtOlvidasteContrasena = findViewById<TextView>(R.id.txtOlvidasteContrasena)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvIniciarconGoogle = findViewById<ImageView>(R.id.imvIniciarconGoogle)
        val imvVerContra3 = findViewById<ImageView>(R.id.imvVerContra3)
        var isPasswordVisible = false
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

        //Validación para campos
        @RequiresApi(Build.VERSION_CODES.P)
        fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
            val typeface = ResourcesCompat.getFontLogin(this, fontResId)
            val spannableString = android.text.SpannableString(errorMessage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spannableString.setSpan(
                    typeface?.let { android.text.style.TypefaceSpan(it) }, 0, spannableString.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            editText.error = spannableString
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
            if (clave.length < 12) {
                txtContrasenaIniciarSesion.error = "La contraseña debe tener al menos 12 caracteres"
                return@setOnClickListener
            }


            // Encriptar la contraseña ingresada
            val contrasenaEncriptada = hashSHA256(clave)

            // Para que con credenciales especificas se inice sesión con admin
            CoroutineScope(Dispatchers.IO).launch {
                val conexion = ClaseConexion().cadenaConexion()

                val query = "SELECT tu.nombre_usuario FROM tbTiposUsuarios tu INNER JOIN tbUsuarios u ON tu.id_tipo_usuario = u.id_tipo_usuario WHERE u.correo = ? AND u.contraseña = ?"
                val statement = conexion?.prepareStatement(query)
                statement?.setString(1, correoIngresado)
                statement?.setString(2, contrasenaEncriptada)
                val resultSet = statement?.executeQuery()

                if (resultSet?.next() == true) {
                    val nombreTipoUsuario = resultSet.getString("nombre_usuario")

                    val siguientePantalla = when (nombreTipoUsuario) {
                        "ADMIN" -> Intent(this@activity_iniciar_sesion, InicioAdmin::class.java)
                        else -> Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                    }
                    // Asignación de valores globales
                    startActivity(siguientePantalla)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@activity_iniciar_sesion, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Iniciar corrutina para ejecutar la consulta de login en segundo plano
            CoroutineScope(Dispatchers.IO).launch {
                val conexion = ClaseConexion().cadenaConexion()

                // Consulta SQL para verificar las credenciales del usuario (es un select)
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

                if (resultSet?.next() == true) {
                    val nombreTipoUsuario = resultSet.getString("nombre_usuario")
                    val siguientePantalla = when (nombreTipoUsuario) {
                        "ADMIN" -> Intent(this@activity_iniciar_sesion, InicioAdmin::class.java)
                        else -> Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                    }
                    startActivity(siguientePantalla)
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
        // Inicio de sesión con google
        imvIniciarconGoogle.setOnClickListener {
            val configuracionGoogle =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()
            val ClienteGoogle = GoogleSignIn.getClient(this, configuracionGoogle)
            startActivityForResult(ClienteGoogle.signInIntent, InicioSesionGoogle)
        }
        //Navegación para poder ir a los métodos de recuperación de contraseña
        txtOlvidasteContrasena.setOnClickListener {
            val siguientepantalla = Intent(this, metodos_contras::class.java)
            startActivity(siguientepantalla)
        }
        //Navegación para ir a la activity anterior
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
            startActivity(volverAtras)
        }

        // Para que los txt tengan la fuente de poppins
        val poppinsFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins)
        txtContrasenaIniciarSesion.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        txtContrasenaIniciarSesion.typeface = poppinsFont

        imvVerContra3.setOnClickListener {
            if (isPasswordVisible) {
                // Si la contraseña es visible, la ocultamos y cambiamos la imagen
                txtContrasenaIniciarSesion.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imvVerContra3.setImageResource(R.drawable.ojocerrado)
            } else {
                // Si la contraseña está oculta, la mostramos y cambiamos la imagen
                txtContrasenaIniciarSesion.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imvVerContra3.setImageResource(R.drawable.ojo)
            }
            // Reaplica la fuente personalizada
            txtContrasenaIniciarSesion.typeface = poppinsFont
            isPasswordVisible = !isPasswordVisible
        }
    }

    // Para que suceda el inicio de sesión con google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InicioSesionGoogle) {
            val tarea = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = tarea.getResult(ApiException::class.java)
                if (cuenta != null) {
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credenciales)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val paginaInicio = Intent(this, PaginaInicio::class.java)
                                startActivity(paginaInicio)
                                overridePendingTransition(0, 0)
                            } else {
                                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_LONG).show()
            }
        }
    }
}
