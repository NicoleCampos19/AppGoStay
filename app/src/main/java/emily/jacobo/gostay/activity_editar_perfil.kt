package emily.jacobo.gostay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import emily.jacobo.gostay.activity_iniciar_sesion.variableGloalLogin.correoIngresado
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.imageView
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.miPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.UUID

class activity_editar_perfil : AppCompatActivity() {

    // Códigos de opción para seleccionar la galería o tomar una foto
    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103

    // Códigos de solicitud para permisos de cámara y almacenamiento
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    // Variables que se inicializarán más tarde
    lateinit var correoActual: String
    lateinit var contrasenaActual: String
    lateinit var txtNewContraP: String
    lateinit var imageView: ImageView
    lateinit var miPath: String

    // Genera un UUID único
    val uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //Variables del companion object de activity_iniciar_sesion
        //correoActual = activity_iniciar_sesion.variableGloalLogin.correoIngresado
       // contrasenaActual = activity_iniciar_sesion.variableGloalLogin.clave

        //Acceder al EditText y obtener el valor de la contraseña como String
        val editTextContra = findViewById<EditText>(R.id.txtContraPerfil)
        txtNewContraP = editTextContra.text.toString()

        //Se mandan a llamar los elementos de la vista
        val imvAtrasPerfil = findViewById<ImageView>(R.id.imvAtrasPerfil)
        val btnGuardarPerfil = findViewById<Button>(R.id.btnGuardarPerfil)
        val imvGaleriaPerfil = findViewById<ImageView>(R.id.imvGaleriaPerfil)
        val imvVerContraPerfil = findViewById<ImageView>(R.id.imvVerContraPerfil)
        imageView = findViewById(R.id.imvPerfil2)
        val imvCamaraPerfil = findViewById<ImageView>(R.id.imvCamaraPerfil)
        var isPasswordVisible = false

        // Obtener SharedPreferences
        val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)


        // Recuperar el correo almacenado
        correoActual = userPreferences.getString("email", "") ?: ""
        contrasenaActual = userPreferences.getString("password", "") ?: ""


        imvGaleriaPerfil.setOnClickListener {
            //Al darle clic al botón de la galeria pedimos los permisos primero
            checkStoragePermission()
        }

        imvCamaraPerfil.setOnClickListener {
            //Al darle clic al botón de la camara pedimos los permisos primero
            checkCameraPermission()
        }

        // Función para cargar la imágen de perfil
        fun cargarImagenperfil(correoIngresado: String) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Realizar la consulta para obtener la imagen
                    println("conexion")
                    val conexion = ClaseConexion().cadenaConexion()
                    println("query")
                    val query = "SELECT imgFoto FROM tbUsuarios WHERE correo = ?"
                    println("antes preparedStatement")
                    val preparedStatement = conexion!!.prepareStatement(query)
                    println("despues preparedStatement")
                    preparedStatement.setString(1, correoIngresado)
                    println("despues del correo")

                    val resultSet = preparedStatement.executeQuery()
                    println("ANTES DEL IF")
                    if (resultSet.next()) {
                        println("DESPUES DEL IF")
                        val imgFotoUrl = resultSet.getString("imgFoto")
                        val imgFotoUrl2 = "https://fotografias.lasexta.com/clipping/cmsimages02/2020/09/21/86828440-B1FB-43AC-9E9C-A94AC6A4B8BD/default.jpg?crop=1300,731,x0,y0&width=1900&height=1069&optimize=low"
                        println(imgFotoUrl)

                        println("urlimg")

                        withContext(Dispatchers.Main) {
                            println("dentro del withContext")

                            Log.d("Perfil", "URL de imagen: $imgFotoUrl")

                            println("url imagen $imgFotoUrl ")

                            println(" antes Glide")
                            Glide.with(this@activity_editar_perfil)
                                .load(imgFotoUrl)
                                .apply(RequestOptions().circleCrop())
                                .into(imageView)
                            println("Glide")
                        }
                        // Si no se encuentra la foto mostrar un toast
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@activity_editar_perfil, "No se encontró la imagen de perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                    resultSet.close()
                    preparedStatement.close()
                    conexion.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@activity_editar_perfil, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        cargarImagenperfil(correoActual)

        //Para que se guarde el nuevo correo y la nueva contraseña
        btnGuardarPerfil.setOnClickListener {

            // Recuperar el correo almacenado
            correoActual = userPreferences.getString("email", "") ?: ""
            contrasenaActual = userPreferences.getString("password", "") ?: ""
            val nuevoCorreo = findViewById<EditText>(R.id.txtCorreoPerfil)
            val nuevaContra = findViewById<EditText>(R.id.txtContraPerfil)
            val correo = correoActual
            val clave = contrasenaActual

            val editTextContra = findViewById<EditText>(R.id.txtContraPerfil)

            val nuevoCorreoTexto = nuevoCorreo.text.toString().trim()
            val nuevaContraTexto = editTextContra.text.toString().trim()
            // Recuperar la contraseña almacenada

// Log para verificar la contraseña recuperada
            Log.d("VALIDACION", "Contraseña recuperada: '$nuevaContra'")
            Log.d("VALIDACION", "Contraseña recuperada: '$txtNewContraP'")


            Log.d("VALIDACION", "Nuevo Correo: '$nuevoCorreo'")
            Log.d("VALIDACION", "Nueva Contraseña: '$nuevaContraTexto'")
            Log.d("VALIDACION", "Correo Actual: '$correoActual'")

            // Validación para campos vacíos
            if (correoActual.isEmpty() || nuevaContraTexto.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación del formato del correo
            if (!Patterns.EMAIL_ADDRESS.matcher(nuevoCorreoTexto).matches()) {
                nuevoCorreo.error = "El correo no tiene un formato válido"
                return@setOnClickListener
            }

            // Validación de la contraseña
            if (nuevaContraTexto.length < 12) {
                editTextContra.error = "La contraseña debe tener al menos 12 caracteres"
                return@setOnClickListener
            }

            if (correo.isNotEmpty() && clave.isNotEmpty()) {
                // Subir imagen a Firebase y obtener la URL
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                subirimagenFirebase(bitmap) { imageUrl ->
                    miPath = imageUrl
                    // Actualizar la imagen en la base de datos Oracle
                    guardarUsuarioConFoto(correo, clave, miPath)
                    // También puedes actualizar el correo y la contraseña si es necesario
                    actualizarCorreo(nuevoCorreo.text.toString(), correoActual)
                    actualizarContraseña(correoActual, nuevaContraTexto)

                    // Navegar a la siguiente pantalla
                    val siguientePantalla = Intent(this, PaginaInicio::class.java)
                    startActivity(siguientePantalla)
                }
                // Para solicitarle que seleccione una foto de perfil
            } else {
                Toast.makeText(
                    this,
                    "Completa todos los campos y selecciona una foto",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        //Para que la letra del ojito salga con la fuente de poppins
        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins)
        editTextContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        editTextContra.typeface = poppinsFont

        imvVerContraPerfil.setOnClickListener {
            if (isPasswordVisible) {
                // Si la contraseña es visible, la ocultamos y cambiamos la imagen
                editTextContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imvVerContraPerfil.setImageResource(R.drawable.ojocerrado)
            } else {
                // Si la contraseña está oculta, la mostramos y cambiamos la imagen
                editTextContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imvVerContraPerfil.setImageResource(R.drawable.ojo)
            }
            // Reaplica la fuente personalizada
            editTextContra.typeface = poppinsFont
            isPasswordVisible = !isPasswordVisible
        }

        // Navegación para ir atrás
        imvAtrasPerfil.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }
        }

    //Encriptación
fun hashSHA256(contrasenaEscrita: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

    //Función para actualizar la contraseña
private fun actualizarContraseña(correo: String, contraseña: String) {

    CoroutineScope(Dispatchers.IO).launch {

        try {
            // Encripta la contraseña que se pasa como parámetro
            val contrasenaEncriptada = hashSHA256(contraseña)

            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
                val query =
                    "UPDATE tbUsuarios SET contraseña = ? WHERE correo = ?"
                val preparedStatement: PreparedStatement = objConexion.prepareStatement(query)
                preparedStatement.setString(1, contrasenaEncriptada)
                preparedStatement.setString(2, correo)
                preparedStatement.executeUpdate()
                preparedStatement.close()

                val commit = objConexion.prepareStatement("commit")
                commit.executeUpdate()
                objConexion.close()
            } else {
                println("No se pudo actualizar la contraseña")
            }

        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }
}

    // Función para poder actualizar el correo
private fun actualizarCorreo(nuevoCorreo: String, correoActual: String) {

    CoroutineScope(Dispatchers.IO).launch {

        try {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
            println("este es el correo con el que hago el select $correoActual")
                // Verificar si el correo actual existe en la base de datos
                val selectQuery = "SELECT id_usuario FROM tbUsuarios WHERE correo = ?"
                val selectStatement = objConexion.prepareStatement(selectQuery)
                selectStatement.setString(1, correoActual)
                val selectResult = selectStatement.executeQuery()

                if (selectResult.next()) {
                    println("Usuario encontrado con el correo: $correoActual")

                    // Si el correo existe, proceder con la actualización
                    val query =
                        "UPDATE tbUsuarios SET correo = ? WHERE id_usuario = (SELECT id_usuario FROM tbUsuarios WHERE correo = ?)"
                    val preparedStatement: PreparedStatement = objConexion.prepareStatement(query)
                    preparedStatement.setString(1, nuevoCorreo)
                    preparedStatement.setString(2, correoActual)
                    preparedStatement.executeUpdate()
                    preparedStatement.close()

                    val commit = objConexion.prepareStatement("commit")
                    commit.executeUpdate()
                    //Si en dado caso no existe un usuario con ese correo
                } else {
                    println("No se encontró ningún usuario con el correo: $correoActual")
                }

                selectStatement.close()
                selectResult.close()

            } else {
                println("No se pudo conectar a la base de datos")
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }
}
    //Función para pedir el persmiso de la cámara
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

    //Función para pedir el permiso de la cámara
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

    //Función para pedir el permiso de la galería
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
                            imageView.setImageURI(it)  // Aquí actualizamos el ImageView
                        }
                    }
                }

                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPath = url
                            imageView.setImageBitmap(it)  // Aquí actualizamos el ImageView
                        }
                    }
                }
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
            Toast.makeText(this@activity_editar_perfil, "Error al subir la imagen", Toast.LENGTH_SHORT).show()

        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }

    // Guardar la imagen en Firebase y actualizar la imagen en la BD
    private fun guardarUsuarioConFoto(correo: String, clave: String, imageUri: String) {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                // Primero, obtenemos el id del usuario
                val selectStatement = objConexion?.prepareStatement("SELECT id_usuario FROM tbUsuarios WHERE correo = ?")!!
                selectStatement.setString(1, correo)
                val resultSet = selectStatement.executeQuery()

                if (resultSet.next()) {
                    val idUsuario = resultSet.getInt("id_usuario")

                    // Actualizamos la URL de la imagen en la base de datos
                    val updateStatement = objConexion.prepareStatement("UPDATE tbUsuarios SET imgFoto = ? WHERE id_usuario = ?")
                    updateStatement.setString(1, imageUri)
                    updateStatement.setInt(2, idUsuario)
                    updateStatement.executeUpdate()
                    updateStatement.close()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@activity_editar_perfil, "Foto de perfil actualizada correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@activity_editar_perfil, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                resultSet.close()
                selectStatement.close()
                objConexion.close()
            }
        } catch (e: SQLException) {
            println("Error al guardar usuario: $e")
        }
    }
}













