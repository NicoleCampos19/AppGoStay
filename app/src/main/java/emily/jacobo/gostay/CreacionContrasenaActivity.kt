package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import oracle.ons.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class CreacionContrasenaActivity : AppCompatActivity() {

    private lateinit var txtNewContra: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creacion_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtNewContra = findViewById(R.id.txtNewContra)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnCrearContrasena = findViewById<Button>(R.id.btnCrearcontrasena)

        btnCrearContrasena.setOnClickListener {
            val nuevaContraseña = txtNewContra.text.toString()

            // Llamada a la función para actualizar la contraseña en la base de datos
           //ctualizarContraseñaEnBD(nuevaContraseña)
        }

        btnCrearContrasena.setOnClickListener {
            val CrearContrasena = Intent(this, PaginaInicio::class.java)
            startActivity(CrearContrasena)
            overridePendingTransition(0, 0)
        }
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Confirmacion_Cuenta::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }
    }

    /*
    private fun actualizarContraseñaEnBD(nuevaContraseña: String) {

        var connection: Connection? = null
        var statement: Statement? = null

        try {
            // Establecer la conexión con la base de datos (reemplaza con tus credenciales y URL de conexión)
            connection = DriverManager.getConnection("jdbc:mysql://tu_servidor:puerto/tu_base_de_datos", "usuario", "contraseña")

            // Crear el statement
            statement = connection.createStatement()

            // ID del usuario cuya contraseña se va a actualizar (reemplaza con el ID del usuario correspondiente)
            val idUsuario = 1  // Ejemplo: ID del usuario Leo

            // Ejecutar la actualización
            val sql = "UPDATE tbUsuarios SET contraseña = '$nuevaContraseña' WHERE id_usuario = $idUsuario"
            val filasAfectadas = statement.executeUpdate(sql)

            if (filasAfectadas > 0) {
                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                // Aquí puedes navegar a la siguiente pantalla si es necesario
            } else {
                Toast.makeText(this, "No se encontró ningún usuario para actualizar", Toast.LENGTH_SHORT).show()
            }

        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
        } finally {
            // Cerrar la conexión y liberar recursos
            try {
                statement?.close()
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
*/
    }
