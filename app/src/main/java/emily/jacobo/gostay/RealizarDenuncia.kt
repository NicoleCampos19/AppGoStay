package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class RealizarDenuncia : AppCompatActivity() {

    // Variable privada que guarda el ID del hotel, inicializada con -1 como valor por defecto.
    private var idHotel: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_realizar_denuncia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mandar a llamar los elementos de la vista
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val radioButton = findViewById<RadioButton>(R.id.radio_button_1)
        val btnAgregarDenuncia = findViewById<Button>(R.id.btnAgregarDenuncias)
        val ID_Hotel = intent.getIntExtra("id_hotel", idHotel)

        println("idHotel recibido: $idHotel")

        // Cuando se hace clic en el botón de agregar denuncia, se ejecuta este bloque.
        btnAgregarDenuncia.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val denunciaTexto = selectedRadioButton.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()

                            val agregarDenuncia = objConexion?.prepareStatement(
                                "INSERT INTO tbDenuncias (nombre_denuncia, id_hoteles) VALUES (?, ?)"
                            )!!
                            agregarDenuncia.setString(1, denunciaTexto)
                            agregarDenuncia.setInt(2, ID_Hotel)
                            agregarDenuncia.executeUpdate()
                            agregarDenuncia.close()

                            withContext(Dispatchers.Main){
                                showCustomDialog()
                            }
                    } catch (ex: Exception) {
                        println(ex.message)
                    }
                }
            }
            overridePendingTransition(0,0)

        }

        // Para ir a la página de inicio
        imvAtrasc.setOnClickListener {
            finish()
            overridePendingTransition(0,0)

        }

        val checkedRadioButtonId = radioGroup.checkedRadioButtonId // Returns View.NO_ID if nothing is checked.
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Responds to child RadioButton checked/unchecked
        }

        // To check a radio button
        radioButton.isChecked = true

        // To listen for a radio button's checked/unchecked state changes
        radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            // Responds to radio button being checked/unchecked
        }
    }

    // Función privada para mostrar un diálogo personalizado en la interfaz.
    private fun showCustomDialog() {
        runOnUiThread {
            val dialog = Dialog(this@RealizarDenuncia)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_denuncia_realizada)

            val btnClose = dialog.findViewById<Button>(R.id.btnDialogClose)
            btnClose.setOnClickListener {
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }
    }
}