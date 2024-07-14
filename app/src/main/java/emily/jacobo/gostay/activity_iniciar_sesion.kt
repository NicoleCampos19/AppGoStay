package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import modelo.tbTipoUsuarios
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.sql.SQLException
import java.util.UUID

class activity_iniciar_sesion : AppCompatActivity() {

    companion object variableGloalLogin{
        private const val InicioSesionGoogle = 100
        val correoIngresado = "admin@gmail.com"
    }

    private lateinit var txtCorreoInciarSesion: EditText
    private lateinit var txtContrasenaIniciarSesion: EditText

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

        txtCorreoInciarSesion = findViewById(R.id.txtCorreoInciarSesion)
        txtContrasenaIniciarSesion = findViewById(R.id.txtContrasenaIniciarSesion)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvIniciarconGoogle = findViewById<ImageView>(R.id.imvIniciarconGoogle)
        val txtOlvidasteContrasena = findViewById<TextView>(R.id.txtOlvidasteContrasena)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnMientras = findViewById<Button>(R.id.btnmientrasxd)


        val correoIngreado = txtCorreoInciarSesion.text.toString().trim()
        val clave = txtContrasenaIniciarSesion.text.toString().trim()
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
        //Para el campo de nombre
        btnIniciar.setOnClickListener {
            val correo = txtCorreoInciarSesion.text.toString()
            val contrasena = txtContrasenaIniciarSesion.text.toString()
            var hayErrores = false

            //Para el campo de correo electrónico
            if(correo.isEmpty()){
                setErrorWithCustomFont(txtCorreoInciarSesion, "Llena este campo", R.font.poppins)
            }
           // if (txtCorreoInciarSesion.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                //setErrorWithCustomFont(txtCorreoInciarSesion, "El correo no tiene un formato válido", R.font.poppins)
           //}

                //Para el campo de contraseña
            if(contrasena.isEmpty()){
                setErrorWithCustomFont(txtContrasenaIniciarSesion, "Llena este campo", R.font.poppins)
            }


            val contrasenaEncriptada = hashSHA256(clave)


                CoroutineScope(Dispatchers.IO).launch {
                    val conexion = ClaseConexion().cadenaConexion()

                    val query = "SELECT tu.nombre_usuario FROM tbTiposUsuarios tu INNER JOIN tbUsuarios u ON tu.id_tipo_usuario = u.id_tipo_usuario WHERE u.correo = ? AND u.contraseña = ?"

                    val statement = conexion?.prepareStatement(query)
                    statement?.setString(1, correoIngreado)
                    statement?.setString(2, contrasenaEncriptada)
                    val resultSet = statement?.executeQuery()

                    if (resultSet?.next() == true) {
                        val nombreTipoUsuario = resultSet.getString("nombre_usuario")

                        // Determinar a qué Activity dirigirse
                        val siguientePantalla = when (nombreTipoUsuario) {
                            "ADMIN" -> Intent(this@activity_iniciar_sesion, InicioAdmin::class.java)
                            else -> Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                        }

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
                val siguientepantalla = Intent(this, RecuperacionCuentaActivity::class.java)
                startActivity(siguientepantalla)
            }

            imvAtrasc.setOnClickListener {
                val volverAtras = Intent(this, activity_registrarse::class.java)
                startActivity(volverAtras)
            }

            btnMientras.setOnClickListener {
                val siguientePantalla = Intent(this, PaginaInicio::class.java)
                startActivity(siguientePantalla)
            }

        }
    private fun hashSHA256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

}



