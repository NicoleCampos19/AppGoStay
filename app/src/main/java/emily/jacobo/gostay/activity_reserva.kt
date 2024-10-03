package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptorTipoHabitacion
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel

import android.widget.ArrayAdapter
import androidx.core.util.Pair as AndroidxPair
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
        // Almacena el CVV de la tarjeta, es opcional (puede ser nulo).
        var cvv: Int? = null
        // Almacena la fecha de caducidad de la tarjeta, es opcional.
        var fechaCaducidad: String? = null
        // Almacena el número de la tarjeta, es opcional.
        var numeroTarjeta: String? = null
        // Almacena el nombre del titular de la tarjeta, es opcional
        var nombreTitular: String? = null
        // Almacena la fecha de entrada de la reserva, es opcional.
        var fechaEntrada: String? = null
        // Almacena la fecha de salida de la reserva, es opcional.
        var fechaSalida: String? = null
        // Almacena el nombre del departamento reservado, se inicializa más tarde.
        lateinit var departamento: String
        // Almacena el ID del departamento, es opcional.
        var idDepartamento: Int? = null
        // Almacena la cantidad de habitaciones reservadas, es opcional.
        var cantidadHabitaciones: Int? = null
        // Almacena una lista de pares de fechas reservadas (fecha de entrada, fecha de salida).
        // Se inicializa como una lista vacía.
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

        // Se mandan a llamar los elementos de la vista
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)
        val txtFechaCaducidad = findViewById<EditText>(R.id.txtFechaCaducidad)
        val txtFechaReserva = findViewById<EditText>(R.id.txtFechaReserva)
        val spDepartamento = findViewById<Spinner>(R.id.spDepartamento)
        val imgVolverAtrars = findViewById<ImageView>(R.id.imgVolverAtrasXD)
        val txtNombreTitular = findViewById<EditText>(R.id.txtNombreTitular)
        val txtNumeroTarjeta = findViewById<EditText>(R.id.txtNumeroTarjeta)
        val txtCVV = findViewById<EditText>(R.id.txtCVV)
        val spCantidadH = findViewById<Spinner>(R.id.spCantidadH)




            // Mostrar el DateRangePicker cuando se hace clic en el campo de fecha
                txtFechaReserva.setOnClickListener {
                    showDateRangePicker { entrada, salida ->
                        txtFechaReserva.setText("$entrada, $salida")
                        fechaEntrada = entrada
                        fechaSalida = salida
                    }
                }



        // Función para obtener el departamento en el que el usuario vive
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
            // Se retorna la lista
            return listaDepartamentos
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Se inicia una nueva coroutine en el Dispatcher de I/O (hilo secundario).
            val listaDepartamentos = obtenerDepartamentos()
            // Crea una lista de los nombres de los departamentos mapeando el campo 'nombre_departamento' de cada elemento.
            val nombresDepartamentos = listaDepartamentos.map { it.nombre_departamento }
            // Cambia al Dispatcher Main (hilo principal) para actualizar la interfaz de usuario.
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

        // Para que se cierre la activity
        imgVolverAtrars.setOnClickListener {
            finish()
        }

        // Configura el DatePickerDialog para la fecha de caducidad
        txtFechaCaducidad.setOnClickListener {
            showDatePickerDialog { date, year, month, day ->
                // Establece el texto en el formato "MM-dd" para mostrar
                txtFechaCaducidad.setText(String.format("%02d-%02d", month + 1, day))
                // Almacena la fecha completa en formato "yyyy-MM-dd"
                fechaCaducidad = String.format("%04d-%02d-%02d", year, month + 1, day)
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
            cantidadHabitaciones = spCantidadH.selectedItem.toString().toInt()
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

    // Mostrar el DateRangePicker con las fechas reservadas bloqueadas y sin permitir selección del día actual ni días anteriores
    private fun showDateRangePicker(onDateSelected: (entrada: String, salida: String) -> Unit) {
        val calendar = Calendar.getInstance()

        // Establece la fecha actual
        val today = calendar.time

        // Ajusta la fecha de inicio al día siguiente
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val minDate = calendar.time  // Día siguiente al actual

        // Configura el DatePickerDialog
        val dateRangePickerDialog = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Selecciona las fechas de reserva")
            .setTheme(R.style.ThemeMaterialCalendar)
            .setSelection(AndroidxPair(minDate.time, minDate.time))  // Usa AndroidxPair aquí
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(minDate.time)  // No permitir selección del día actual ni días anteriores
                    .setValidator(object : CalendarConstraints.DateValidator {
                        override fun describeContents(): Int {
                            TODO("Not yet implemented")
                        }

                        override fun writeToParcel(dest: Parcel, flags: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun isValid(date: Long): Boolean {
                            // Solo permitir selección a partir del día siguiente
                            return date >= minDate.time
                        }
                    })
                    .build()
            )
            .build()

        dateRangePickerDialog.addOnPositiveButtonClickListener { selection ->
            val dateRange = selection as AndroidxPair<Long, Long>  // Asegúrate de usar AndroidxPair aquí
            val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateRange.first))
            val salida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateRange.second))
            onDateSelected(entrada, salida)
        }

        // Muestra el DateRangePicker
        dateRangePickerDialog.show(supportFragmentManager, "dateRangePicker")
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

    // Función para cargar el departamento (se hace un select)
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
        val cantidadList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        // Crear el ArrayAdapter usando la lista de números
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cantidadList)

        // Especificar el layout a usar cuando la lista aparece desplegada
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        spinner.adapter = adapter
    }

    private fun showDatePickerDialog(onDateSet: (String, Int, Int, Int) -> Unit) {
        // Obtiene una instancia del calendario con la fecha y hora actuales.
        val calendar = Calendar.getInstance()
        // Define el formato de fecha como "yyyy-MM-dd".
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Obtiene la fecha actual a partir del calendario.
        val currentDate = calendar.time
        // Crea un cuadro de diálogo para seleccionar la fecha (DatePickerDialog).
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                // Llama a la función onDateSet con la fecha completa
                onDateSet(dateFormat.format(calendar.time), year, month, day)
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

