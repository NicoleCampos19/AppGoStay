package emily.jacobo.gostay

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast


class activity_reserva : AppCompatActivity() {
    private lateinit var txtEntradaSalida: EditText
    private lateinit var txtSalida: EditText
    private lateinit var txtFechaCaducidad: EditText
    private lateinit var txtCVV: EditText
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
        val idTipoHabitacion = intent.getIntExtra("id_tipo_habitacion", -1)
        txtEntradaSalida = findViewById(R.id.txtEntradaSalida)
        txtSalida = findViewById(R.id.txtSalidaxd)
        txtFechaCaducidad = findViewById(R.id.txtFechaCaducidad)
        txtCVV = findViewById(R.id.txtCVV)

        // Configura el DatePickerDialog para la fecha de entrada
        txtEntradaSalida.setOnClickListener {
            showDatePickerDialog { date ->
                txtEntradaSalida.setText(date)
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
        txtCVV.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches(Regex("\\d*"))) null else ""
        })

        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)
        btnSiguiente.setOnClickListener {
            val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(txtEntradaSalida.text.toString())
            val salida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(txtSalida.text.toString())
            val fechaActual = Calendar.getInstance().time

            if (entrada == null || salida == null) {
                showToast("Por favor seleccione ambas fechas.")
                return@setOnClickListener
            }

            if (entrada < fechaActual) {
                showToast("La fecha de entrada no puede ser menor a la fecha actual.")
                return@setOnClickListener
            }

            if (salida <= entrada) {
                showToast("La fecha de salida debe ser después de la fecha de entrada.")
                return@setOnClickListener
            }

            // Procesar el formulario aquí
        }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
