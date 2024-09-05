package emily.jacobo.gostay

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbDepartamentos


class activity_reserva : AppCompatActivity() {

    companion object {
        var cvv: Int? = null
        var fechaCaducidad: String? = null
        var numeroTarjeta: String? = null
        var nombreTitular: String? = null
        var fechaEntrada: String? = null
        var fechaSalida: String? = null
        lateinit  var departamento: String
        var idDepartamento: Int? = null
    }







    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reserva)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }






        //#queremoscodigolimpio
        setupCantidadSpinner()

        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)
        val txtFechaCaducidad = findViewById<EditText>(R.id.txtFechaCaducidad)

        val txtEntrada = findViewById<EditText>(R.id.txtEntrada)
        val txtSalida = findViewById<EditText>(R.id.txtSalida)

        val spDepartamento = findViewById<Spinner>(R.id.spDepartamento)

        val imgVolverAtrars = findViewById<ImageView>(R.id.imgVolverAtrasXD)


        imgVolverAtrars.setOnClickListener {
            finish()
        }

        // Configura el DatePickerDialog para la fecha de entrada
        txtEntrada.setOnClickListener {
            showDatePickerDialog { date ->
                txtEntrada.setText(date)
            }
        }

        // Configura el DatePickerDialog para la fecha de salida
        txtSalida.setOnClickListener {
            showDatePickerDialog { date ->
                txtSalida.setText(date)
            }
        }

        // Configura el DatePickerDialog para la fecha de caducidad
        txtFechaCaducidad.setOnClickListener {
            showDatePickerDialog { date ->
                txtFechaCaducidad.setText(date)
            }
        }

        // Valida el CVV para permitir solo números


        btnSiguiente.setOnClickListener {
            departamento = spDepartamento.selectedItem.toString()
            fechaCaducidad = txtFechaCaducidad.text.toString()
            val cvvText = findViewById<EditText>(R.id.txtCVV).text.toString()
            val numeroTarjetaText = findViewById<EditText>(R.id.txtNumeroTarjeta).text.toString()
            nombreTitular = findViewById<EditText>(R.id.txtNombreTitular).text.toString()
            fechaEntrada = txtEntrada.text.toString()
            fechaSalida = txtSalida.text.toString()



            // Validar fechas
            if (fechaEntrada!!.isNotEmpty() && fechaSalida!!.isNotEmpty()) {
                val dateEntrada = SimpleDateFormat("yyyy-MM-dd").parse(fechaEntrada)
                val dateSalida = SimpleDateFormat("yyyy-MM-dd").parse(fechaSalida)

                if (dateEntrada.after(dateSalida)) {
                    Toast.makeText(this, "La fecha de entrada no puede ser mayor que la fecha de salida.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                Toast.makeText(this, "Las fechas de entrada y salida no pueden estar vacías.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            if (nombreTitular!!.isEmpty()) {
                Toast.makeText(this, "El nombre del titular no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación del número de tarjeta
            if (numeroTarjetaText.length < 16 || !numeroTarjetaText.all { it.isDigit() }) {
                Toast.makeText(this, "El número de tarjeta debe tener 16 dígitos y solo contener números", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            numeroTarjeta = numeroTarjetaText

            if (cvvText.length != 3 || !cvvText.all { it.isDigit() }) {
                Toast.makeText(this, "El CVV debe tener exactamente 3 dígitos y solo contener números", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cvv = cvvText.toInt()

            if (txtFechaCaducidad.text.isNotEmpty() && departamento.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val idDepto = cargaridDepartamento(departamento) // Obtener el ID del departamento de manera síncrona

                    withContext(Dispatchers.Main) {
                        if (idDepto != null) {
                            idDepartamento = idDepto
                            // Si el idDepartamento se ha obtenido correctamente, procede a la siguiente Activity
                            val intent = Intent(this@activity_reserva, activity_confirmacionReserva::class.java)
                            startActivity(intent)
                        } else {
                            // Manejar el caso en el que no se pueda obtener el ID del departamento
                            Toast.makeText(this@activity_reserva, "Error al obtener el ID del departamento", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Algunos campos están mal ingresados o vacíos", Toast.LENGTH_SHORT).show()
            }


        }



        fun obtenerDepartamentos(): List<tbDepartamentos> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbDepartamentos")!!
            val listaDepartamentos = mutableListOf<tbDepartamentos>()
            while (resultSet.next()) {
                val id_departamento = resultSet.getInt("id_departamento")
                val nombre_departamento = resultSet.getString("nombre_departamento")

                val valoresJuntos = tbDepartamentos(id_departamento, nombre_departamento)
                listaDepartamentos.add(valoresJuntos)


            }
            return listaDepartamentos
        }

        CoroutineScope(Dispatchers.IO).launch {
            val listaDepartamentos = obtenerDepartamentos()
            val nombresDepartamentos = listaDepartamentos.map { it.nombre_departamento }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@activity_reserva, android.R.layout.simple_spinner_dropdown_item, nombresDepartamentos)


                spDepartamento.adapter = adapter
            }
        }




    }
    //buscar id departamento por nombre
    private fun obteneridDepartamentoEnVal(departamento: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val idDepartamentoxd = cargaridDepartamento(departamento)
            withContext(Dispatchers.Main) {

                idDepartamento = idDepartamentoxd

            }
        }
    }
    private fun cargaridDepartamento(departamento: String): Int? {
        var idDepartamento: Int? = null
        val conexion = ClaseConexion().cadenaConexion()

        val query = """
        SELECT id_departamento FROM tbDepartamentos WHERE nombre_departamento = ?
    """
        val statement = conexion?.prepareStatement(query)
        statement?.setString(1, departamento)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            idDepartamento = resultSet.getInt("id_departamento")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return idDepartamento
    }










    private fun setupCantidadSpinner() {
        val spinner = findViewById<Spinner>(R.id.spCantidadH)

        // Lista de números del 1 al 5
        val cantidadList = listOf(1, 2, 3, 4, 5)

        // Crear el ArrayAdapter usando la lista de números
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cantidadList)

        // Especificar el layout a usar cuando la lista aparece desplegada
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        spinner.adapter = adapter
    }




    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = calendar.time

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                onDateSet(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Configura el rango de fechas
        datePickerDialog.datePicker.minDate = currentDate.time

        datePickerDialog.show()
    }



}
