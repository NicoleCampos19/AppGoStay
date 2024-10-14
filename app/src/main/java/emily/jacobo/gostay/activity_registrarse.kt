package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.app.Activity
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import emily.jacobo.gostay.RecuperacionCuentaActivity.variablesGobalesRecuperacion
import emily.jacobo.gostay.RecuperacionCuentaActivity.variablesGobalesRecuperacion.Correo
import emily.jacobo.gostay.activity_iniciar_sesion.variableGloalLogin
import kotlinx.coroutines.CoroutineScope
import org.checkerframework.checker.regex.qual.Regex
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.util.UUID

class activity_registrarse : AppCompatActivity() {

    // Códigos de solicitud para permisos de cámara y almacenamiento
    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    // Para que se genere un código random para el uuid
    val uuid = UUID.randomUUID().toString()

    // Variables que se inicializarán más tarde
    companion object variableGloalLogin{
        lateinit var txtCorreoI: EditText
        lateinit var txtContraI: EditText
        lateinit var imageView: ImageView
        var miPath: String = "https://img.freepik.com/vector-premium/icono-perfil-avatar-predeterminado-imagen-usuario-redes-sociales-icono-avatar-gris-silueta-perfil-blanco-ilustracion-vectorial_561158-3383.jpg"
        var CodigoRegis = (100000..999999).random()
    }

    private val InicioSesionGoogle = 100
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrarse)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Mando a llamar todos los elementos
        val tipousuario: Int = 1
        val txtIniciarsesion = findViewById<TextView>(R.id.txtIniciaSesion)
        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        val txtApellido = findViewById<TextView>(R.id.txtApellido)
        val txtFechaNacimiento = findViewById<TextView>(R.id.txtFechaNacimiento)
        txtCorreoI = findViewById(R.id.txtCorreoElectronico)
        val txtTelefono = findViewById<TextView>(R.id.txtTelefono)
        txtContraI = findViewById(R.id.txtContrasenaRegistrarse)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val imvIniciargoogle = findViewById<ImageView>(R.id.imvIniciarGoogle)
        val imvVerContra1 = findViewById<ImageView>(R.id.imvVerContra4)
        val imvVerContra2 = findViewById<ImageView>(R.id.imvVerContra2)
        val txtConfirmarContraRegis = findViewById<TextView>(R.id.txtConfirmarContraRegis)
        imageView = findViewById(R.id.imvPerfilRegis)
        val imvGaleria = findViewById<ImageView>(R.id.imvGaleria)
        val imvCamara = findViewById<ImageView>(R.id.imvCamara)
        var isPasswordVisible = false

        //Encriptación
        fun hashSHA256(contrasenaEscrita: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        imvGaleria.setOnClickListener {
            //Al darle clic al botón de la galeria pedimos los permisos primero
            checkStoragePermission()
        }

        imvCamara.setOnClickListener {
            //Al darle clic al botón de la camara pedimos los permisos primero
            checkCameraPermission()
        }

        //Mostrar calendario en el txtFechaNacimiento

        txtFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            // Calcular la fecha máxima (hace 18 años a partir de hoy)
            val fechaMaxima = Calendar.getInstance()
            fechaMaxima.set(anio - 18, mes, dia)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaNacimiento.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            // Configurar la fecha máxima a hace 18 años a partir de hoy
            datePickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis
            datePickerDialog.show()
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

        //Convertir a String las variables
        btnRegistrarse.setOnClickListener {
            Log.e("Registro", "Iniciando proceso de registro")
            val idTipoUsuario = tipousuario
            val nombre = txtNombre.text.toString()
            val apellido = txtApellido.text.toString()
            val telefono = txtTelefono.text.toString()
            val fechanacimiento = txtFechaNacimiento.text.toString()
            val correo = txtCorreoI.text.toString()
            val contrasena = txtContraI.text.toString()
            val password = txtContraI.text.toString()
            val confirmPassword = txtConfirmarContraRegis.text.toString()

            var hayVacios = false
            var hayErrores = false

            //Para el campo de nombre
            if(nombre.isEmpty()){
                setErrorWithCustomFont(txtNombre, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (!nombre.matches(Regex("^[a-zA-Z]+$"))) {
                setErrorWithCustomFont(txtNombre, "El nombre contiene solo letras", R.font.poppins)
                hayErrores = true
            }
            //Para el campo de apellido
            else if(apellido.isEmpty()){
                setErrorWithCustomFont(txtApellido, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (!apellido.matches(Regex("^[a-zA-Z]+$"))) {
                setErrorWithCustomFont(txtApellido, "El apellido debe contener letras", R.font.poppins)
                hayErrores = true
            }
            //Para el campo de fecha nacimiento
            else if(fechanacimiento.isEmpty()){
                setErrorWithCustomFont(txtFechaNacimiento, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            //Para el campo de correo
            else if(correo.isEmpty()){
                setErrorWithCustomFont(txtCorreoI, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (!correo.matches (Regex("[a-zA-Z0-9._-]+@[a-z]+[.][a-z]+"))) {
                setErrorWithCustomFont(txtCorreoI, "El formato del correo no es válido", R.font.poppins)
                hayErrores = true
            }
            //Para el campo de telefono
            else if(telefono.isEmpty()){
                setErrorWithCustomFont(txtTelefono, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (telefono.length != 8) {
                setErrorWithCustomFont(txtTelefono, "El teléfono solo debe contener 8 carácteres", R.font.poppins)
                hayErrores = true
            }
            //Para el campo de contraseña
            else if(contrasena.isEmpty()){
                setErrorWithCustomFont(txtContraI, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (contrasena.length < 12) {
                setErrorWithCustomFont(txtContraI, "La contraseña debe contener 12 carácteres", R.font.poppins)
                hayErrores = true
            }
            // Validar que las contraseñas coinciden
            if (password != confirmPassword) {
                setErrorWithCustomFont(txtConfirmarContraRegis, "Las contraseñas no coinciden", R.font.poppins)
                hayErrores = true
            }



            // Si hay errores, no procede
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG).show()
            } else {
                // Generar código de verificación
                CodigoRegis = (100000..999999).random() // Genera el código
                val htmlCorreo = generarHTMLCorreo(CodigoRegis.toString()) // Usa el mismo código para el correo
                GlobalScope.launch(Dispatchers.IO) {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val contrasenaEncriptada = hashSHA256(txtContraI.text.toString())
                    val crearUsuario = objConexion?.prepareStatement(
                        "INSERT INTO tbUsuarios(nombre_usuario, apellido, fecha_nacimiento, correo, telefono, contraseña, id_tipo_usuario, imgFoto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    )!!
                    crearUsuario.setString(1, txtNombre.text.toString())
                    crearUsuario.setString(2, txtApellido.text.toString())
                    crearUsuario.setString(3, txtFechaNacimiento.text.toString())
                    crearUsuario.setString(4, txtCorreoI.text.toString())
                    crearUsuario.setString(5, txtTelefono.text.toString())
                    crearUsuario.setString(6, contrasenaEncriptada)
                    crearUsuario.setInt(7, idTipoUsuario)
                    crearUsuario.setString(8, miPath)
                    Log.e("Imagen", "$miPath")
                    crearUsuario.executeUpdate()

                    // Enviar correo con el código de verificación
                    //enviarCorreo(correo, "Código de Verificación", htmlCorreo)
                    Log.d("Registro", "Código de recuperación: $CodigoRegis")
                    CoroutineScope(Dispatchers.Main).launch {
                        enviarCorreo(correo, "Confirmación de contraseña", htmlCorreo)
                    }

                    //Toast para mostrar que el usuario fue creado
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@activity_registrarse, "Usuario creado", Toast.LENGTH_LONG).show()
                        txtCorreoI.setText("")
                        txtContraI.setText("")
                        imageView.setImageResource(0)
                        imageView.tag = null
                    }
                }
                // Navegación para la siguiente pantalla
                val siguientepantalla = Intent(this, activity_ConfirmarCorreo::class.java)
                startActivity(siguientepantalla)
            }
        }

        // Inicio de sesión con google
        imvIniciargoogle.setOnClickListener {
            val configuracionGoogle =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()

            val ClienteGoogle = GoogleSignIn.getClient(this, configuracionGoogle)
            startActivityForResult(ClienteGoogle.signInIntent, InicioSesionGoogle)
        }

        //Navegación para ir a la activity de inicio de sesión
        txtIniciarsesion.setOnClickListener {
            val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(siguientepantalla)
        }

        //Para que el txt al darle al ojito tenga poppins
        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins)

        imvVerContra1.setOnClickListener {
            if (isPasswordVisible) {
                // Si la contraseña es visible, la ocultamos y cambiamos la imagen
                txtContraI.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imvVerContra1.setImageResource(R.drawable.ojocerrado)
            } else {
                // Si la contraseña está oculta, la mostramos y cambiamos la imagen
                txtContraI.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imvVerContra1.setImageResource(R.drawable.ojo)
            }
            // Reaplica la fuente personalizada
            txtContraI.typeface = poppinsFont
            isPasswordVisible = !isPasswordVisible
        }

        imvVerContra2.setOnClickListener {
            if (isPasswordVisible) {
                // Si la contraseña es visible, la ocultamos y cambiamos la imagen
                txtConfirmarContraRegis.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imvVerContra2.setImageResource(R.drawable.ojocerrado)
            } else {
                // Si la contraseña está oculta, la mostramos y cambiamos la imagen
                txtConfirmarContraRegis.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imvVerContra2.setImageResource(R.drawable.ojo)
            }
            // Reaplica la fuente personalizada
            txtConfirmarContraRegis.typeface = poppinsFont
            isPasswordVisible = !isPasswordVisible
        }
    }

    //Función para que al mandar el código de recuperación contenga diseño
    fun generarHTMLCorreo(CodigoRegis: String): String {
        return """
        <!DOCTYPE HTML>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {font-family: 'Lato', sans-serif; background-color: #f9f9f9; color: #000;}
                .code {padding: 15px 30px; font-size: 24px; background-color: #5cb5c4; border-radius: 8px; margin-bottom: 50px;}
            </style>
        </head>
        <body>
            <h2>¡Bienvenido a GoStay!</h2>
            <p>Estamos comprobando que tu correo sea una cuenta existente</p>
            <p>Por favor, ingresa el siguiente código: </p>
            <div class="code">$CodigoRegis</div>
            <p>Equipo GoStay</p>
        </body>
        </html>
    """.trimIndent()
    }

    //Función para el permiso de la cámara
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está aceptado, entonces se lo pedimos
            pedirPermisoCamara()
        } else {
            //El permiso ya está aceptado
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, codigo_opcion_tomar_foto)
        }
    }
    //Función para pedir el permiso
    private fun pedirPermisoCamara() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
        ) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA),CAMERA_REQUEST_CODE
            )
        }
    }

    //Función para pedir el permiso de la cámara
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está aceptado, entonces se lo pedimos
            pedirPermisoAlmacenamiento()
        } else {
            //El permiso ya está aceptado
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, codigo_opcion_galeria)
        }
    }

    //Función para habilitar el permiso
    private fun pedirPermisoAlmacenamiento() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),STORAGE_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El permiso está aceptado, entonces Abrimos la camara:
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, codigo_opcion_tomar_foto)
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una alerta/Toast.
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El permiso está aceptado, entonces Abrimos la galeria
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, codigo_opcion_galeria)
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una alerta/Toast.
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }
    //Subir la imagen a Firebase Storage
    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this@activity_registrarse, "Error al subir la imagen", Toast.LENGTH_SHORT).show()

        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Llama al método del super para mantener el comportamiento predeterminado.
        super.onActivityResult(requestCode, resultCode, data)
        // Verifica si el resultado fue exitoso (RESULT_OK).
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // Si la opción seleccionada fue de galería de imágenes.
                codigo_opcion_galeria -> {
                    val imageUri: Uri? = data?.data
                    // Obtiene la URI de la imagen seleccionada.
                    imageUri?.let {
                        // Sube la imagen a Firebase y guarda la URL.
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url ->
                            miPath = url
                            imageView.setImageURI(it)
                        }
                    }
                }
                // Si la opción seleccionada fue tomar una foto.
                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPath = url
                            imageView.setImageBitmap(it)
                        }
                    }
                }
                // Si el usuario selecciona iniciar sesión con Google.
                InicioSesionGoogle -> {
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
                                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}