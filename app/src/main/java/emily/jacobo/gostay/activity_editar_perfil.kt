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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
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

    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    val uuid = UUID.randomUUID().toString()


    lateinit var correoActual: String
    lateinit var contrasenaActual: String
    lateinit var txtNewContraP: String
    lateinit var NuevaFoto: ImageView
    lateinit var miPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NuevaFoto = findViewById<ImageView>(R.id.imvPerfil2)


        NuevaFoto = activity_registrarse.variableGloalLogin.imageView

        //  variables del companion object de activity_iniciar_sesion
        correoActual = activity_iniciar_sesion.variableGloalLogin.txtCorreoInciarSesionV
        contrasenaActual = activity_iniciar_sesion.variableGloalLogin.txtContrasenaIniciarSesionV

        // Acceder al EditText y obtener el valor de la contraseña como String
        val editTextContra = findViewById<EditText>(R.id.txtContraPerfil)
        txtNewContraP = editTextContra.text.toString()

        val imvAtrasPerfil = findViewById<ImageView>(R.id.imvAtrasPerfil)
        val btnGuardarPerfil = findViewById<Button>(R.id.btnGuardarPerfil)
        val imvGaleriaPerfil = findViewById<ImageView>(R.id.imvGaleriaPerfil)
        val imvCamaraPerfil = findViewById<ImageView>(R.id.imvCamaraPerfil)

        imvGaleriaPerfil.setOnClickListener {
            //Al darle clic al botón de la galeria pedimos los permisos primero
            checkStoragePermission()
        }

        imvCamaraPerfil.setOnClickListener {
            //Al darle clic al botón de la camara pedimos los permisos primero
            checkCameraPermission()
        }


        btnGuardarPerfil.setOnClickListener{

            val nuevoCorreo = findViewById<EditText>(R.id.txtCorreoPerfil).text.toString()
//            val bitmap = (NuevaFoto.drawable as BitmapDrawable).bitmap // Convertir la imagen en un Bitmap

            // Actualizar el correo y la contraseña utilizando las funciones ya definidas
            actualizarCorreo(nuevoCorreo, correoActual)
            actualizarContraseña(correoActual, txtNewContraP)



          /*  // Subir la imagen a Firebase y luego actualizar la URL en la base de datos
            actualizarImagenFirebase(this, bitmap) { imageUrl ->
                actualizarImagenUrlEnBD(correoActual, imageUrl)
            }*/


            val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
             startActivity(siguientepantalla)


        }


        imvAtrasPerfil.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }


        }

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
                // Por si   hay algún problema
            }
        }

}


fun hashSHA256(contrasenaEscrita: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

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



private fun actualizarCorreo(nuevoCorreo: String, correoActual: String) {

    CoroutineScope(Dispatchers.IO).launch {

        try {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
            println("este es el correo con el que hago ele select $correoActual")
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

private fun actualizarImagenFirebase(context: Context, bitmap: Bitmap, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()
    val uploadTask = imageRef.putBytes(data)

    uploadTask.addOnFailureListener { exception ->
        Toast.makeText(context, "Error al actualizar la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
    }.addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error al obtener la URL de la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun actualizarImagenUrlEnBD(correoActual: String, imageUrl: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
                val query = "UPDATE tbUsuarios SET imgFoto = ? WHERE correo = ?"
                val preparedStatement: PreparedStatement = objConexion.prepareStatement(query)
                preparedStatement.setString(1, imageUrl)
                preparedStatement.setString(2, correoActual)
                preparedStatement.executeUpdate()
                preparedStatement.close()

                val commit = objConexion.prepareStatement("commit")
                commit.executeUpdate()
                objConexion.close()
            } else {
                println("No se pudo conectar a la base de datos")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
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
                            NuevaFoto.setImageURI(it)
                        }
                    }
                }
                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPath = url
                            NuevaFoto.setImageBitmap(it)
                        }
                    }
                }
                emily.jacobo.gostay.activity_iniciar_sesion.InicioSesionGoogle -> {
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













