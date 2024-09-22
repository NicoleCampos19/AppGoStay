package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.storage
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.txtContraI
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.txtCorreoI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class activity_iniciar_sesion : AppCompatActivity() {

    companion object variableGloalLogin{
        val InicioSesionGoogle = 100
        lateinit var correoIngresado: String
        lateinit var txtCorreoInciarSesionV: String
        lateinit var txtContrasenaIniciarSesionV: String

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Definición de EditText
        val txtCorreoInciarSesion = findViewById<EditText>(R.id.txtCorreoRecu)
        val txtContrasenaIniciarSesion = findViewById<EditText>(R.id.txtContrasenaIniciarSesion)
        val txtOlvidasteContrasena = findViewById<TextView>(R.id.txtOlvidasteContrasena)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvIniciarconGoogle = findViewById<ImageView>(R.id.imvIniciarconGoogle)
        val imvVerContra3 = findViewById<ImageView>(R.id.imvVerContra3)
        var isPasswordVisible = false

        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //Validación para campos
        @RequiresApi(Build.VERSION_CODES.P)
        fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
            val typeface = ResourcesCompat.getFont(this, fontResId)
            val spannableString = android.text.SpannableString(errorMessage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spannableString.setSpan(
                    typeface?.let { android.text.style.TypefaceSpan(it) }, 0, spannableString.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            editText.error = spannableString
        }

        btnIniciar.setOnClickListener {
            txtCorreoInciarSesionV = txtCorreoInciarSesion.text.toString()
            txtContrasenaIniciarSesionV = txtContrasenaIniciarSesion.text.toString()

            // Validación de campos
            val clave = txtContrasenaIniciarSesion.text.toString().trim()
            correoIngresado = txtCorreoInciarSesion.text.toString()

            if (correoIngresado.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!correoIngresado.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoInciarSesion.error = "El correo no tiene un formato válido"
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
                    val nombreTipoUsuario = resultSet.getString("nombre_usuario")

                    val siguientePantalla = when (nombreTipoUsuario) {
                        "ADMIN" -> Intent(this@activity_iniciar_sesion, InicioAdmin::class.java)
                        else -> Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                    }
                    // Asignación de valores globales
                    startActivity(siguientePantalla)
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

        imvIniciarconGoogle.setOnClickListener {
            val configuracionGoogle =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()

            val ClienteGoogle = GoogleSignIn.getClient(this, configuracionGoogle)
            startActivityForResult(ClienteGoogle.signInIntent, InicioSesionGoogle)
        }

        txtOlvidasteContrasena.setOnClickListener {
            val siguientepantalla = Intent(this, metodos_contras::class.java)
            startActivity(siguientepantalla)
        }

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
            startActivity(volverAtras)
        }

        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins)

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





