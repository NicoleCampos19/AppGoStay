package emily.jacobo.gostay

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        spValoracionG.onItemSelectedListener = createOnItemSelectedListener()

        // Limpieza
        val spValoracionLimpi: Spinner = findViewById(R.id.spValoracionLimpi)
        spValoracionLimpi.adapter = adapter
        spValoracionLimpi.onItemSelectedListener = createOnItemSelectedListener()

        // Ubicación
        val spValoracionUbi: Spinner = findViewById(R.id.spValoracionUbi)
        spValoracionUbi.adapter = adapter
        spValoracionUbi.onItemSelectedListener = createOnItemSelectedListener()

        // Personal
        val spValoracionPersonal: Spinner = findViewById(R.id.spValoracionPersonal)
        spValoracionPersonal.adapter = adapter
        spValoracionPersonal.onItemSelectedListener = createOnItemSelectedListener()

        // Instalaciones
        val spValoracionInstalaciones: Spinner = findViewById(R.id.spValoracionInstalaciones)
        spValoracionInstalaciones.adapter = adapter
        spValoracionInstalaciones.onItemSelectedListener = createOnItemSelectedListener()

        // Aquí sigue lo demás de la actividad
    }

    private fun createOnItemSelectedListener(): AdapterView.OnItemSelectedListener? = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Realizar alguna acción con el valor seleccionado por el usuario
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Opcional: Manejar caso cuando no se selecciona ningún elemento
        }
    }
}
