package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptorTipoHabitacion
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbDepartamentos
import java.util.Date


class activity_reserva : AppCompatActivity() {

    companion object {
        var cvv: Int? = null
        var fechaCaducidad: String? = null
        var numeroTarjeta: String? = null
        var nombreTitular: String? = null
        var fechaEntrada: String? = null
        var fechaSalida: String? = null
        lateinit var departamento: String
        var idDepartamento: Int? = null
        var fechasReservadas: List<Pair<String, String>> =
            emptyList()  // Para guardar las fechas reservadas
    }



    @SuppressLint("MissingInflatedId")
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
        val txtFechaReserva = findViewById<EditText>(R.id.txtFechaReserva)
        val spDepartamento = findViewById<Spinner>(R.id.spDepartamento)
        val imgVolverAtrars = findViewById<ImageView>(R.id.imgVolverAtrasXD)
        val txtNombreTitular = findViewById<EditText>(R.id.txtNombreTitular)
        val txtNumeroTarjeta = findViewById<EditText>(R.id.txtNumeroTarjeta)
        val txtCVV = findViewById<EditText>(R.id.txtCVV)
        val spCantidadH = findViewById<Spinner>(R.id.spCantidadH)

        // Obtener las fechas reservadas de la base de datos antes de mostrar el DateRangePicker
        CoroutineScope(Dispatchers.IO).launch {
            val tipoHabitacionId =
                AdaptorTipoHabitacion.idTipoHabitacionGlobal // Supongo que ya tienes el ID del tipo de habitación seleccionado
            fechasReservadas = obtenerFechasReservadas(tipoHabitacionId)

            withContext(Dispatchers.Main) {
                // Mostrar el DateRangePicker cuando se hace clic en el campo de fecha
                txtFechaReserva.setOnClickListener {
                    showDateRangePicker(fechasReservadas) { entrada, salida ->
                        txtFechaReserva.setText("$entrada, $salida")
                        fechaEntrada = entrada
                        fechaSalida = salida
                    }
                }
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
                val adapter = ArrayAdapter(
                    this@activity_reserva,
                    android.R.layout.simple_list_item_1,
                    nombresDepartamentos
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spDepartamento.adapter = adapter
            }
        }

        imgVolverAtrars.setOnClickListener {
            finish()
        }

        // Configura el DatePickerDialog para la fecha de caducidad
        txtFechaCaducidad.setOnClickListener {
            showDatePickerDialog { date ->
                txtFechaCaducidad.setText(date)
            }
        }

        //Validación para campos
        @RequiresApi(Build.VERSION_CODES.P)
        fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
            val typeface = ResourcesCompat.getFont(this, fontResId)
            val spannableString = android.text.SpannableString(errorMessage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spannableString.setSpan(
                    typeface?.let { android.text.style.TypefaceSpan(it) },
                    0,
                    spannableString.length,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            editText.error = spannableString
        }

        btnSiguiente.setOnClickListener {
            departamento = spDepartamento.selectedItem.toString()
            fechaCaducidad = txtFechaCaducidad.text.toString()
            // Obtener el número de tarjeta y CVV como números
            val numeroTarjetaText = txtNumeroTarjeta.text.toString()
            val cvvText = txtCVV.text.toString()
            nombreTitular = findViewById<EditText>(R.id.txtNombreTitular).text.toString()


            // Obtener el texto completo del campo de reserva
            val reservaText = txtFechaReserva.text.toString()


// Variables para controlar los errores
            var hayVacios = false
            var hayErrores = false

// Validación para txtFechaReserva: Verificar que no esté vacío y que las fechas de entrada y salida sean válidas
            if (fechaEntrada.isNullOrEmpty() || fechaSalida.isNullOrEmpty()) {
                setErrorWithCustomFont(txtFechaReserva, "Llena este campo", R.font.poppins)
                hayVacios = true
            } else {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fechaEntradaDate = sdf.parse(fechaEntrada)
                val fechaSalidaDate = sdf.parse(fechaSalida)

                // Verificar que la fecha de entrada no sea mayor a la fecha de salida
                if (fechaEntradaDate.after(fechaSalidaDate)) {
                    setErrorWithCustomFont(
                        txtFechaReserva,
                        "La fecha de entrada no puede ser mayor a la fecha de salida",
                        R.font.poppins
                    )
                    hayErrores = true
                } else {
                    txtFechaReserva.error = null  // Limpiar el error si las fechas son válidas
                }
            }


// Validación para nombreTitular: No vacío y longitud máxima de 30 caracteres
            if (nombreTitular!!.isEmpty()) {
                setErrorWithCustomFont(
                    txtNombreTitular,
                    "El nombre del titular no puede estar vacío.",
                    R.font.poppins
                )
                hayVacios = true
            } else if (nombreTitular!!.length > 30) {
                setErrorWithCustomFont(
                    txtNombreTitular,
                    "El nombre del titular no debe exceder 30 caracteres.",
                    R.font.poppins
                )
                hayErrores = true
            }

// Validación para numeroTarjeta: No vacío, longitud de 16 dígitos, y permite guiones
            if (txtNumeroTarjeta.text.isEmpty()) {
                setErrorWithCustomFont(
                    txtNumeroTarjeta,
                    "El número de tarjeta no puede estar vacío.",
                    R.font.poppins
                )
                hayVacios = true
            } else if (!numeroTarjetaText.matches(Regex("^\\d{16}\$")) && !numeroTarjetaText.matches(
                    Regex("^\\d{4}-\\d{4}-\\d{4}-\\d{4}\$")
                )
            ) {
                setErrorWithCustomFont(
                    txtNumeroTarjeta,
                    "El número de tarjeta debe tener 16 dígitos",
                    R.font.poppins
                )
                hayErrores = true
            }


// Validación para fechaCaducidad: Verificar que no esté vacía y que sea una fecha válida
            if (txtFechaCaducidad.text.isEmpty()) {
                setErrorWithCustomFont(
                    txtFechaCaducidad,
                    "La fecha de caducidad no puede estar vacía.",
                    R.font.poppins
                )
                hayVacios = true
            } else {
                txtFechaCaducidad.error = null  // Limpiar el error si la fecha es válida
            }

// Validación para CVV: No vacío y exactamente 3 dígitos
            if (cvvText.isEmpty()) {
                setErrorWithCustomFont(txtCVV, "El CVV no puede estar vacío.", R.font.poppins)
                hayVacios = true
            } else if (cvvText.length != 3 || !cvvText.all { it.isDigit() }) {
                setErrorWithCustomFont(
                    txtCVV,
                    "El CVV debe tener exactamente 3 dígitos.",
                    R.font.poppins
                )
                hayErrores = true
            }

// Si hay vacíos o errores, mostrar mensaje y no proceder
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                // Si no hay errores, proceder con la lógica
                numeroTarjeta = numeroTarjetaText.replace(
                    "-",
                    ""
                )  // Remover los guiones para procesar el número
                cvv = cvvText.toInt()

                CoroutineScope(Dispatchers.IO).launch {
                    val idDepto =
                        cargaridDepartamento(departamento) // Obtener el ID del departamento de manera síncrona

                    withContext(Dispatchers.Main) {
                        if (idDepto != null) {
                            idDepartamento = idDepto
                            // Si el idDepartamento se ha obtenido correctamente, procede a la siguiente Activity
                            val intent = Intent(
                                this@activity_reserva,
                                activity_confirmacionReserva::class.java
                            )
                            startActivity(intent)
                        } else {
                            // Manejar el caso en el que no se pueda obtener el ID del departamento
                            Toast.makeText(
                                this@activity_reserva,
                                "Error al obtener el ID del departamento",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }


        }

    }

    // Método para obtener las fechas reservadas de la base de datos
    private suspend fun obtenerFechasReservadas(idTipoHabitacion: Int): List<Pair<String, String>> {
        val conexion = ClaseConexion().cadenaConexion()
        val query = "SELECT entrada, salida FROM tbHabitaciones WHERE id_tipo_habitacion = ?"
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idTipoHabitacion)
        val resultSet = statement?.executeQuery()
        val fechas = mutableListOf<Pair<String, String>>()

        while (resultSet?.next() == true) {
            val entrada = resultSet.getString("entrada")
            val salida = resultSet.getString("salida")
            fechas.add(Pair(entrada, salida)) // Guardar las fechas reservadas
        }

        resultSet?.close()
        statement?.close()
        conexion?.close()

        return fechas
    }

    // Mostrar el DateRangePicker con las fechas reservadas bloqueadas y sin permitir fechas anteriores a hoy
    private fun showDateRangePicker(
        fechasReservadas: List<Pair<String, String>>,
        onDatesSelected: (String, String) -> Unit
    ) {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.ThemeMaterialCalendar)
            .setTitleText("Seleccione fecha de entrada y salida")
            .setCalendarConstraints(configureCalendarConstraints(fechasReservadas))  // Pasar las fechas reservadas para deshabilitarlas
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val entrada = dateFormat.format(selection.first)
            val salida = dateFormat.format(selection.second)
            onDatesSelected(entrada, salida)
        }

        dateRangePicker.show(supportFragmentManager, "date_range_picker")
    }

    // Configurar las restricciones del calendario para no permitir fechas anteriores a hoy y bloquear fechas reservadas
    private fun configureCalendarConstraints(fechasReservadas: List<Pair<String, String>>): CalendarConstraints {
        val today = Calendar.getInstance()  // Fecha actual

        val dateValidator = object : CalendarConstraints.DateValidator {
            override fun isValid(date: Long): Boolean {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = dateFormat.format(Date(date))

                // Verificar si la fecha es anterior a hoy
                if (date < today.timeInMillis) {
                    return false
                }

                // Deshabilitar si la fecha está dentro de los rangos reservados
                for (fecha in fechasReservadas) {
                    val entrada = fecha.first
                    val salida = fecha.second
                    if (currentDate in entrada..salida) {
                        return false  // Deshabilitar esta fecha
                    }
                }

                return true  // Permitir la selección si no está reservada y no es anterior a hoy
            }

            override fun describeContents(): Int = 0

            override fun writeToParcel(dest: Parcel, flags: Int) {}
        }

        return CalendarConstraints.Builder()
            .setValidator(dateValidator)
            .setStart(today.timeInMillis)  // Establecer la fecha mínima como hoy
            .build()
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

