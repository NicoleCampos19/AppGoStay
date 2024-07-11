package emily.jacobo.gostay

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
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.util.UUID

class activity_editar_perfil : AppCompatActivity() {

    companion object n
    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103

    lateinit var imageView: ImageView
    lateinit var miPath:String
    lateinit var txtCorreoP: EditText
    lateinit var txtContraP: EditText
    val id_usuario = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imvFoto)
        txtCorreoP = findViewById(R.id.txtCorreoPerfil)
        txtContraP = findViewById(R.id.txtContraPerfil)
        val imvAtrasPerfil = findViewById<ImageView>(R.id.imvAtrasPerfil)
        val btnGuardarPerfil = findViewById<Button>(R.id.btnGuardarPerfil)
        val btnHacerFoto = findViewById<Button>(R.id.btnHacerFoto)
        val btnGaleria = findViewById<Button>(R.id.btnGaleria)

        imvAtrasPerfil.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }


        btnGaleria.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, codigo_opcion_galeria)
        }

        btnHacerFoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, codigo_opcion_tomar_foto)
        }


        btnGuardarPerfil.setOnClickListener {
            val correo = txtCorreoP.text.toString().trim()
            val clave = txtContraP.text.toString().trim()
            val imageUri = miPath

            if (correo.isNotEmpty() && clave.isNotEmpty() && imageUri != null) {
                guardarUsuarioConFoto(correo, clave, imageUri)
            } else {
                Toast.makeText(this, "Completa todos los campos y selecciona una foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Esta función onActivityResult se encarga de capturar lo que pasa al abrir la geleria o la camara
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

    //Subir la imagen a Firebase Storage
    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${id_usuario}.jpg")
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



    private fun guardarUsuarioConFoto(correo: String, clave: String, imageUri: String) {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val statement =
                    objConexion?.prepareStatement("INSERT INTO tbMisUsuarios (UUID, correo, contraseña, url_imagen) VALUES (?, ?, ?, ?)")!!
                statement.setString(1, id_usuario)
                statement.setString(2, correo)
                statement.setString(2, clave)
                statement.setString(3, imageUri)
                statement.executeUpdate()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@activity_editar_perfil, "Datos guardados", Toast.LENGTH_SHORT).show()
                    txtCorreoP.text.clear()
                    txtContraP.text.clear()
                    imageView.setImageResource(0)
                    imageView.tag = null
                }


            }
        } catch (e: SQLException) {
            println("Error al guardar usuario: $e")
        }



    }



}