package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
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
        // General
        val spValoracionG: Spinner = findViewById(R.id.spValoracionG)
        val items = listOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spValoracionG.adapter = adapter
        spValoracionG.onItemSelectedListener = createOnItemSelectedListener { valoracion ->
            valoracionG = valoracion
            calcularPromedio()
        }


        // Limpieza
        val spValoracionLimpi: Spinner = findViewById(R.id.spValoracionLimpi)
        spValoracionLimpi.adapter = adapter
        spValoracionLimpi.onItemSelectedListener = createOnItemSelectedListener { valoracion ->
            valoracionLimpi = valoracion
            calcularPromedio()
        }

        // Ubicación
        val spValoracionUbi: Spinner = findViewById(R.id.spValoracionUbi)
        spValoracionUbi.adapter = adapter
        spValoracionUbi.onItemSelectedListener = createOnItemSelectedListener { valoracion ->
            valoracionUbi = valoracion
            calcularPromedio()
        }

        // Personal
        val spValoracionPersonal: Spinner = findViewById(R.id.spValoracionPersonal)
        spValoracionPersonal.adapter = adapter
        spValoracionPersonal.onItemSelectedListener = createOnItemSelectedListener { valoracion ->
            valoracionPersonal = valoracion
            calcularPromedio()
        }

        // Instalaciones
        val spValoracionInstalaciones: Spinner = findViewById(R.id.spValoracionInstalaciones)
        spValoracionInstalaciones.adapter = adapter
        spValoracionInstalaciones.onItemSelectedListener = createOnItemSelectedListener { valoracion ->
            valoracionInstalaciones = valoracion
            calcularPromedio()
        }
    }
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
                // Opcional: Manejar caso cuando no se selecciona ningún elemento
            }
        }
    }

    private fun calcularPromedio() {
        val valores = listOf(valoracionG, valoracionLimpi, valoracionUbi, valoracionPersonal, valoracionInstalaciones)
        val valoracionesValidas = valores.filterNotNull()

        if (valoracionesValidas.size == 5) {
            val promedio = valoracionesValidas.average()
            mostrarResultado(promedio)
        }
    }

    private fun mostrarResultado(promedio: Double) {
        // Crear un intent para iniciar activity_hotel_detalles
        val intent = Intent(this, hotel_detalles::class.java)
        // Pasar el valor del promedio a la siguiente actividad
        intent.putExtra("PROMEDIO_VALORACION", promedio)
        startActivity(intent)
    }
}



