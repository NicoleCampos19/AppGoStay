package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.sql.SQLException
import java.util.UUID

class activity_iniciar_sesion : AppCompatActivity() {

    companion object variableGloalLogin{
        private val InicioSesionGoogle = 100
        val codigo_opcion_galeria = 102
        val codigo_opcion_tomar_foto = 103

        val correoIngresado = "leo_monte@gmail.com"
        lateinit var imageView: ImageView
        lateinit var miPath: String
        lateinit var txtCorreoI: EditText
        lateinit var txtContraI: EditText

        val uuid = UUID.randomUUID().toString()
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

        txtCorreoI = findViewById(R.id.txtCorreoInciarSesion)
        txtContraI = findViewById(R.id.txtContrasenaIniciarSesion)

        val txtOlvidasteContrasena = findViewById<TextView>(R.id.txtOlvidasteContrasena)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvIniciarconGoogle = findViewById<ImageView>(R.id.imvIniciarconGoogle)
        val btnMientras = findViewById<Button>(R.id.btnmientrasxd)
        val imvCamaraIni = findViewById<ImageView>(R.id.imvCamaraIni)
        val imvGaleriaIni = findViewById<ImageView>(R.id.imvGaleriaIni)
        imageView = findViewById(R.id.imvFotoIni)

        imvGaleriaIni.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, codigo_opcion_galeria)
        }

        imvCamaraIni.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, codigo_opcion_tomar_foto)
        }

        btnMientras.setOnClickListener {
            val siguientePantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientePantalla)
        }


        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnIniciar.setOnClickListener {
            // Validación de campos
            var hayErrores = false

            val correoIngreado = txtCorreoI.text.toString().trim()
            val clave = txtContraI.text.toString().trim()
            val imageUri = miPath

            if (!correoIngresado.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoI.error = "El correo no tiene un formato válido"
                hayErrores = true
            } else {
                txtCorreoI.error = null
            }

            if (clave.length <= 4) {
                txtContraI.error = "La contraseña debe tener al menos 12 caracteres"
                hayErrores = true
            } else {
                txtContraI.error = null
            }

            // Si hay errores, no procede a guardar los datos
            if (hayErrores) {
                // Hacer algo si hay errores
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val objConexion = ClaseConexion().cadenaConexion()

                    val contraseniaEncriptada =
                        hashSHA256(txtContraI.text.toString())

                    val comprobarUsuario =
                        objConexion?.prepareStatement("SELECT * FROM tbUsuarios WHERE correo = ? AND contraseña = ?")!!
                    comprobarUsuario.setString(1, txtCorreoI.text.toString())
                    comprobarUsuario.setString(2, contraseniaEncriptada)
                    val resultado = comprobarUsuario.executeQuery()
                    // Si encuentra un resultado
                    if (resultado?.next() == true) {
                        val esAdmin = correoIngresado == "admin@gmail.com"
                        val siguientePantalla = if (esAdmin) {
                            Intent(this@activity_iniciar_sesion, InicioAdmin::class.java)
                        } else {
                            Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                        }
                        startActivity(siguientePantalla)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@activity_iniciar_sesion,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("contraseña $contraseniaEncriptada")
                        }
                    }
                }

                if (correoIngresado.isNotEmpty() && clave.isNotEmpty() && imageUri != null) guardarUsuarioConFoto(
                    correoIngresado, clave, imageUri) else Toast.makeText(
                    this,
                    "Completa todos los campos y selecciona una foto",
                    Toast.LENGTH_SHORT
                ).show()
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                codigo_opcion_galeria -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url ->
                            miPath = url
                            imageView.setImageURI(it)
                        }
                    }
                }

                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPath = url
                            imageView.setImageBitmap(it)
                        }
                    }
                }
            }
        }
    }

    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@activity_iniciar_sesion,
                "Error al subir la imagen",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }

    private fun guardarUsuarioConFoto(correo: String, clave: String, imageUri: String) {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val statement =
                    objConexion?.prepareStatement("INSERT INTO tbMisUsuarios (UUID, correo, contraseña, url_imagen) VALUES (?, ?, ?, ?)")!!
                statement.setString(1, uuid)
                statement.setString(2, correo)
                statement.setString(3, clave)
                statement.setString(4, imageUri)
                statement.executeUpdate()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@activity_iniciar_sesion,
                        "Datos guardados",
                        Toast.LENGTH_SHORT
                    ).show()
                    txtCorreoI.text.clear()
                }
            }
        } catch (e: SQLException) {
            println("Error al guardar usuario: $e")
        }
    }

}
