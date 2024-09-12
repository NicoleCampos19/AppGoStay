package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_resenas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resenas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        // Configuración de Spinners
        val spValoracionG: Spinner = findViewById(R.id.spValoracionG)
        val spValoracionLimpi: Spinner = findViewById(R.id.spValoracionLimpi)
        val spValoracionUbi: Spinner = findViewById(R.id.spValoracionUbi)
        val spValoracionPersonal: Spinner = findViewById(R.id.spValoracionPersonal)
        val spValoracionInstalaciones: Spinner = findViewById(R.id.spValoracionInstalaciones)

        val items = listOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //adaptadores
        spValoracionG.adapter = adapter
        spValoracionLimpi.adapter = adapter
        spValoracionUbi.adapter = adapter
        spValoracionPersonal.adapter = adapter
        spValoracionInstalaciones.adapter = adapter

        //configuracion spinners
        spValoracionG.onItemSelectedListener = createOnItemSelectedListener { valoracionG = it }
        spValoracionLimpi.onItemSelectedListener = createOnItemSelectedListener { valoracionLimpi = it }
        spValoracionUbi.onItemSelectedListener = createOnItemSelectedListener { valoracionUbi = it }
        spValoracionPersonal.onItemSelectedListener = createOnItemSelectedListener { valoracionPersonal = it }
        spValoracionInstalaciones.onItemSelectedListener = createOnItemSelectedListener { valoracionInstalaciones = it }

        // Botón Guardar
        val btnGuardar: Button = findViewById(R.id.btn_guardarR)
        btnGuardar.setOnClickListener {
            if (validarSpinnersLlenos()) {
                calcularPromedio() // Calcular el promedio solo al hacer clic en el botón si todos los spinners están llenos
            } else {
                mostrarMensaje("Por favor, selecciona una valoración para todos los criterios.")
            }
        }
    }


    //declaraciones
    private var valoracionG: Int? = null
    private var valoracionLimpi: Int? = null
    private var valoracionUbi: Int? = null
    private var valoracionPersonal: Int? = null
    private var valoracionInstalaciones: Int? = null

    private fun createOnItemSelectedListener(onValueSelected: (Int) -> Unit): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString().toIntOrNull()
                selectedItem?.let { onValueSelected(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //si no se selecciona nada :)
            }
        }
    }

    private fun validarSpinnersLlenos(): Boolean {
        // validacion
        return valoracionG != null &&
                valoracionLimpi != null &&
                valoracionUbi != null &&
                valoracionPersonal != null &&
                valoracionInstalaciones != null
    }

    private fun calcularPromedio() {
        val valores = listOf(valoracionG, valoracionLimpi, valoracionUbi, valoracionPersonal, valoracionInstalaciones)
        val promedio = valores.filterNotNull().average()
        mostrarMensaje("El promedio es: $promedio")
        startActivity(intent)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}



