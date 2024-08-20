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
        var cvv: String? = null
    }

    private lateinit var txtNumeroTarjeta: EditText
    private lateinit var txtEntrada: EditText
    private lateinit var txtSalida: EditText
    private lateinit var txtFechaCaducidad: EditText
    private lateinit var txtNombreTitular: EditText
    private lateinit var txtCvv: EditText




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
        val spDepartamento = findViewById<Spinner>(R.id.spDepartamento)
        val idTipoHabitacion = intent.getIntExtra("id_tipo_habitacion", -1)
        val txtCorreoInciarSesionV = activity_iniciar_sesion.txtCorreoInciarSesionV

        val idHotel = PaginaInicio.hotelIdGlobal
        val idDepartamento = spDepartamento.selectedItemPosition

        txtCvv = findViewById(R.id.txtCVV)
        txtNombreTitular = findViewById(R.id. txtNombreTitular)
        txtNumeroTarjeta = findViewById(R.id.txtNumeroTarjeta)
        txtEntrada = findViewById(R.id.txtEntrada)
        txtSalida  = findViewById(R.id.txtSalida)
        txtFechaCaducidad = findViewById(R.id.txtFechaCaducidad)


        val nombreUsuario = buscarNombreUsuarioPorCorreo(txtCorreoInciarSesionV)
        val idUsuario = buscarIdUsuarioPorCorreo(txtCorreoInciarSesionV)














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
            val entrada = txtEntrada.text.toString()
            val salida = txtSalida.text.toString()
            val fechaCaducidad = txtFechaCaducidad.text.toString()
            val numeroTarjeta = txtNumeroTarjeta.text.toString()
            val nombreTitular = txtNombreTitular.text.toString()
            cvv = findViewById<EditText>(R.id.txtCVV).text.toString()



            if (entrada.isNotEmpty() && salida.isNotEmpty() && fechaCaducidad.isNotEmpty()&&
                numeroTarjeta.isNotEmpty() && nombreTitular.isNotEmpty()) {
                val intent = Intent(this, activity_confirmacionReserva::class.java).apply {
                    intent.putExtra("id_tipo_habitacion", idTipoHabitacion)
                    intent.putExtra("numero_tarjeta", numeroTarjeta)
                    intent.putExtra("nombre_titular", nombreTitular)
                    intent.putExtra("id_departamento", idDepartamento)
                    intent.putExtra("entrada", entrada)
                    intent.putExtra("salida", salida)
                    intent.putExtra("fechaCaducidad", fechaCaducidad)
                    intent.putExtra("id_usuario", idUsuario)

                    intent.putExtra("nombre_usuario", nombreUsuario)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Algunos campos estan mal ingresados o vacios", Toast.LENGTH_SHORT).show()
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
    private fun buscarNombreUsuarioPorCorreo(correo: String): String? {
        var nombreUsuario: String? = null
        val query = "SELECT nombre FROM tbUsuarios WHERE correo = ?"
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.prepareStatement(query).apply {
                    setString(1, correo)
                }
                statement.use { preparedStatement ->
                    val resultSet = preparedStatement.executeQuery()
                    if (resultSet.next()) {
                        nombreUsuario = resultSet.getString("nombre")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception to debug
        }
        return nombreUsuario
    }

    private fun buscarIdUsuarioPorCorreo(correo: String): Int {
        var idUsuario = -1
        val query = "SELECT id_usuario FROM tbUsuarios WHERE correo = ?"
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.prepareStatement(query).apply {
                    setString(1, correo)
                }
                statement.use { preparedStatement ->
                    val resultSet = preparedStatement.executeQuery()
                    if (resultSet.next()) {
                        idUsuario = resultSet.getInt("id_usuario")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception to debug
        }
        return idUsuario
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
