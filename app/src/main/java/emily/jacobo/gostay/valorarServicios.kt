package emily.jacobo.gostay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import emily.jacobo.gostay.PaginaInicio.Companion.hotelIdGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion

class valorarServicios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_valorar_servicios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imvAtrasServicios = findViewById<ImageView>(R.id.imvAtrasServicios)

        imvAtrasServicios.setOnClickListener {
            finish()
        }

        val rbLimpieza = findViewById<RatingBar>(R.id.rbLimpieza)
        val rbUbicacion = findViewById<RatingBar>(R.id.rbUbicacion)
        val rbPersonal = findViewById<RatingBar>(R.id.rbPersonal)
        val rbInstalaciones = findViewById<RatingBar>(R.id.rbInstalaciones)

        val idHotelGlobal = PaginaInicio.hotelIdGlobal

        var ratingLimpieza = 0

        var ratingUbicacion = 0

        var ratingPersonal = 0

        var ratingInstalaciones = 0

        rbInstalaciones.setOnRatingBarChangeListener { rbInstalaciones, fl, b ->
            ratingInstalaciones = fl.toInt() // Convierte el valor flotante en entero (1 a 5)
        }

        rbLimpieza.setOnRatingBarChangeListener { rbLimpieza, fl, b ->
            ratingLimpieza = fl.toInt() // Convierte el valor flotante en entero (1 a 5)
        }

        rbUbicacion.setOnRatingBarChangeListener { rbUbicacion, fl, b ->
            ratingUbicacion = fl.toInt() // Convierte el valor flotante en entero (1 a 5)
        }

        rbPersonal.setOnRatingBarChangeListener { rbPersonal, fl, b ->
            ratingPersonal = fl.toInt() // Convierte el valor flotante en entero (1 a 5)
        }


        fun enviarValoracion() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()

                    // Inserta las valoraciones de los servicios uno por uno
                    // Limpieza
                    val sentenciaLimpieza = objConexion?.prepareStatement(
                        "INSERT INTO tbValoracionOtrosServicios (id_servicio, id_hoteles, id_calificación) VALUES (?, ?, ?)"
                    )
                    sentenciaLimpieza?.setInt(1, 1) // ID del servicio Limpieza
                    sentenciaLimpieza?.setInt(2, hotelIdGlobal!!)
                    sentenciaLimpieza?.setInt(3, ratingLimpieza)
                    sentenciaLimpieza?.executeUpdate()

                    // Ubicación
                    val sentenciaUbicacion = objConexion?.prepareStatement(
                        "INSERT INTO tbValoracionOtrosServicios (id_servicio, id_hoteles, id_calificación) VALUES (?, ?, ?)"
                    )
                    sentenciaUbicacion?.setInt(1, 2) // ID del servicio Ubicación
                    sentenciaUbicacion?.setInt(2, hotelIdGlobal!!)
                    sentenciaUbicacion?.setInt(3, ratingUbicacion)
                    sentenciaUbicacion?.executeUpdate()

                    // Personal
                    val sentenciaPersonal = objConexion?.prepareStatement(
                        "INSERT INTO tbValoracionOtrosServicios (id_servicio, id_hoteles, id_calificación) VALUES (?, ?, ?)"
                    )
                    sentenciaPersonal?.setInt(1, 3) // ID del servicio Personal
                    sentenciaPersonal?.setInt(2, hotelIdGlobal!!)
                    sentenciaPersonal?.setInt(3, ratingPersonal)
                    sentenciaPersonal?.executeUpdate()

                    // Instalaciones
                    val sentenciaInstalaciones = objConexion?.prepareStatement(
                        "INSERT INTO tbValoracionOtrosServicios (id_servicio, id_hoteles, id_calificación) VALUES (?, ?, ?)"
                    )
                    sentenciaInstalaciones?.setInt(1, 4) // ID del servicio Instalaciones
                    sentenciaInstalaciones?.setInt(2, hotelIdGlobal!!)
                    sentenciaInstalaciones?.setInt(3, ratingInstalaciones)
                    sentenciaInstalaciones?.executeUpdate()

                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
            }
        }


        val btnCalificar = findViewById<Button>(R.id.btnCalificar)

        btnCalificar.setOnClickListener {
            if (ratingLimpieza > 0 || ratingPersonal > 0 || ratingUbicacion > 0 || ratingInstalaciones > 0) {
                enviarValoracion()
                showCustomDialog()
            } else {
                Toast.makeText(this@valorarServicios, "Por favor, selecciona al menos una estrella", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showCustomDialog() {
        runOnUiThread {
            val dialog = Dialog(this@valorarServicios)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_calificar_servicios)

            val btnClose = dialog.findViewById<Button>(R.id.btnNoQuiero)
            btnClose.setOnClickListener {
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }
    }
}