package emily.jacobo.gostay

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash_Screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val correoIngresado = sharedPreferences.getString("email", null)

        GlobalScope.launch(Dispatchers.Main) {
            delay(3000)

            if (correoIngresado != null) {
                // Si ya hay un correo guardado, el usuario está logueado, redirigir a PaginaInicio
                startActivity(Intent(this@Splash_Screen, PaginaInicio::class.java))
            } else {
                // Si el usuario no está logueado, verificar si es la primera vez que usa la app
                val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
                if (isFirstTime) {
                    startActivity(Intent(this@Splash_Screen, Bienvenida::class.java))

                    // Actualizar SharedPreferences para que no vuelva a mostrar la bienvenida
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isFirstTime", false)
                    editor.apply()
                } else {
                    startActivity(Intent(this@Splash_Screen, activity_iniciar_sesion::class.java))
                }
            }

            finish()
        }
    }
}